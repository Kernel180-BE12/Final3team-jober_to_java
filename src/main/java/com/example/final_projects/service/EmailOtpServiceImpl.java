package com.example.final_projects.service;

import com.example.final_projects.dto.auth.EmailOtpDtos.*;
import com.example.final_projects.entity.EmailOtp;
import com.example.final_projects.entity.VerifyEmailToken;
import com.example.final_projects.repository.EmailOtpRepository;
import com.example.final_projects.repository.VerifyEmailTokenRepository;
import com.example.final_projects.support.MailService;
import com.example.final_projects.support.OtpCrypto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class EmailOtpServiceImpl implements EmailOtpService{

    private final EmailOtpRepository otpRepo;
    private final VerifyEmailTokenRepository tokenRepo;
    private final OtpCrypto otpCrypto;
    private final MailService mailService;

    private final String pepper;
    private final int codeLength;
    private final int ttlSeconds;
    private final int tokenTtlSeconds;
    private final int resendMinSeconds;
    private final int maxResendPerHour;
    private final int maxAttempts;

    private final Random rng = new SecureRandom();

    public EmailOtpServiceImpl(EmailOtpRepository otpRepo,
                               VerifyEmailTokenRepository tokenRepo,
                               OtpCrypto otpCrypto,
                               MailService mailService,
                               @Value("${security.otp.pepper}") String pepper,
                               @Value("${security.otp.code-length:6}") int codeLength,
                               @Value("${security.otp.ttl-seconds:300}") int ttlSeconds,
                               @Value("${security.otp.token-ttl-seconds:600}") int tokenTtlSeconds,
                               @Value("${security.otp.resend-min-seconds:60}") int resendMinSeconds,
                               @Value("${security.otp.max-resend-per-hour:5}") int maxResendPerHour,
                               @Value("${security.otp.max-attempts:5}") int maxAttempts) {
        this.otpRepo = otpRepo;
        this.tokenRepo = tokenRepo;
        this.otpCrypto = otpCrypto;
        this.mailService = mailService;
        this.pepper = pepper;
        this.codeLength = codeLength;
        this.ttlSeconds = ttlSeconds;
        this.tokenTtlSeconds = tokenTtlSeconds;
        this.resendMinSeconds = resendMinSeconds;
        this.maxResendPerHour = maxResendPerHour;
        this.maxAttempts = maxAttempts;
    }

    @Override
    public void requestOtp(RequestOtpRequest req){
        String email = norm(req.email());
        LocalDateTime now = LocalDateTime.now();

        List<EmailOtp> hist = otpRepo.findAllByEmailOrderByIdDesc(email);
        EmailOtp latest = hist.isEmpty() ? null : hist.get(0);

        if (latest != null) {
            long sec = Duration.between(latest.getLastSentAt(), now).getSeconds();
            if (sec < resendMinSeconds) {
                throw new IllegalArgumentException("재발송은 " + (resendMinSeconds - sec) + "초 후에 가능합니다.");
            }
            if (latest.getResendCount() >= maxResendPerHour &&
                    Duration.between(latest.getLastSentAt(), now).toHours() < 1) {
                throw new IllegalArgumentException("재발송 한도를 초과했습니다. 잠시 후 다시 시도하세요.");
            }
        }

        String code = genNumeric(codeLength);                 // 예: "493201"
        String hash = otpCrypto.sha256WithPepper(code, pepper);

        EmailOtp row = new EmailOtp();
        row.setEmail(email);
        row.setCodeHash(hash);
        row.setExpiresAt(now.plusSeconds(ttlSeconds));
        row.setVerified(false);
        row.setAttemptCount(0);
        row.setLastSentAt(now);
        row.setResendCount((latest != null && Duration.between(latest.getLastSentAt(), now).toHours() < 1)
                ? latest.getResendCount() + 1 : 0);

        otpRepo.save(row);

        // 실제 메일 전송 (템플릿/제목은 프로젝트 표준에 맞춰 조정)
        mailService.send(
                email,
                "[Jober] 이메일 인증 코드",
                "아래 6자리 코드를 5분 내에 입력하세요: " + code
        );}
    @Override
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest req){
        String email = norm(req.email());
        String code = req.code().trim();
        LocalDateTime now = LocalDateTime.now();

        EmailOtp candidate = otpRepo.findAllByEmailOrderByIdDesc(email).stream()
                .filter(e -> !e.isVerified() && e.getExpiresAt().isAfter(now))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효한 OTP가 없습니다. 다시 발송해 주세요."));

        if (candidate.getAttemptCount() >= maxAttempts) {
            throw new IllegalArgumentException("시도 한도를 초과했습니다. OTP를 재발송해 주세요.");
        }
        otpRepo.incrementAttempt(candidate.getId());

        // 해시 비교(상수시간 비교)
        if (!otpCrypto.verify(code, candidate.getCodeHash(), pepper)) {
            throw new IllegalArgumentException("OTP 코드가 올바르지 않습니다.");
        }

        // ✅ 핵심: pre-signup 검증토큰을 기존 테이블(VerifyEmailToken)에 INSERT
        String vtoken = UUID.randomUUID().toString();
        LocalDateTime vexp = now.plusSeconds(tokenTtlSeconds);

        VerifyEmailToken vt = new VerifyEmailToken();
        vt.setToken(vtoken);
        vt.setUser(null);          // 회원가입 전이라 NULL
        // 아래 두 줄을 위해 VerifyEmailToken 엔티티에 setter/getter가 있어야 함
        vt.setEmail(email);        // ← 엔티티에 setEmail(String) 필요
        vt.setPreSignup(true);     // ← 엔티티에 setPreSignup(boolean) 필요
        vt.setExpiresAt(vexp);
        vt.setUsed(false);
        tokenRepo.save(vt);

        // OTP 레코드 표시(선택)
        candidate.setVerified(true);

        long expiresIn = Duration.between(now, vexp).getSeconds();
        return new VerifyOtpResponse(vtoken, expiresIn);}
    @Override
    public void assertValidVerificationTokenForSignup(String email, String verificationToken){
        String normEmail = norm(email);
        VerifyEmailToken t = tokenRepo.findByToken(verificationToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 이메일 검증 토큰입니다."));

        // 아래 접근자들(getEmail/isPreSignup 등)이 엔티티에 구현되어 있어야 함
        if (t.isUsed()) throw new IllegalArgumentException("이미 사용된 검증 토큰입니다.");
        if (!t.isPreSignup()) throw new IllegalArgumentException("가입 전 검증용 토큰이 아닙니다.");
        if (t.getExpiresAt() == null || t.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("검증 토큰이 만료되었습니다.");
        if (t.getEmail() == null || !t.getEmail().equalsIgnoreCase(normEmail))
            throw new IllegalArgumentException("이메일과 검증 토큰이 일치하지 않습니다.");
    }

    private String norm(String email){ return email.trim().toLowerCase(); }
    private String genNumeric(int n){
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(rng.nextInt(10));
        return sb.toString();
    }

}
