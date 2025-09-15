package com.example.final_projects.service;

import com.example.final_projects.dto.auth.*;
import com.example.final_projects.entity.RefreshToken;
import com.example.final_projects.entity.User;
import com.example.final_projects.entity.VerifyEmailToken;
import com.example.final_projects.repository.RefreshTokenRepository;
import com.example.final_projects.repository.UserRepository;
import com.example.final_projects.repository.VerifyEmailTokenRepository;
import com.example.final_projects.security.JwtTokenProvider;
import com.example.final_projects.support.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
    @Transactional
    public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;
        private final RefreshTokenRepository refreshTokenRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtTokenProvider jwtTokenProvider;
        private final VerifyEmailTokenRepository verifyEmailTokenRepository;
        private final MailService mailService;
        private final String verifyBaseUrl;
        private final EmailOtpService emailOtpService;

        public AuthServiceImpl(UserRepository userRepository,
                               RefreshTokenRepository refreshTokenRepository,
                               PasswordEncoder passwordEncoder,
                               JwtTokenProvider jwtTokenProvider,
                               VerifyEmailTokenRepository verifyEmailTokenRepository,
                               MailService mailService,
                               EmailOtpService emailOtpService,
                               @Value("${security.verify.base-url}") String verifyBaseUrl) {
            this.userRepository = userRepository;
            this.refreshTokenRepository = refreshTokenRepository;
            this.passwordEncoder = passwordEncoder;
            this.jwtTokenProvider = jwtTokenProvider;
            this.verifyEmailTokenRepository = verifyEmailTokenRepository;
            this.mailService = mailService;
            this.verifyBaseUrl = verifyBaseUrl;
            this.emailOtpService = emailOtpService;
        }

        /**
         * 회원가입 흐름
         * 1) 이메일 중복 체크 (existsByEmail)
         * 2) User 엔티티 생성: password 해시(BCrypt), 기본 상태/잠금값 세팅
         * 3) 저장 후 ID 반환
         * - 주의: 이메일 대소문자 정규화(선택), UNIQUE 제약 충돌 예외 처리
         */
        @Override
        public SignupResponse signup(SignupRequest req) {
            final String email = req.getEmail().trim().toLowerCase();
            emailOtpService.assertValidVerificationTokenForSignup(email, req.getEmailVerificationToken());
            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }

            User user = new User();
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(req.getPassword()));

                try {
                    var nameField = User.class.getDeclaredField("name");
                    nameField.setAccessible(true);
                    nameField.set(user, req.getName());
                } catch (NoSuchFieldException | IllegalAccessException ignored) {}


            // 초기 상태값
            try {
                var statusField = User.class.getDeclaredField("status");
                statusField.setAccessible(true);
                statusField.set(user, Enum.valueOf(
                        (Class<Enum>) statusField.getType(),
                        "ACTIVE"
                ));
            } catch (Exception ignore) {}

            try {
                var lockedField = User.class.getDeclaredField("locked");
                lockedField.setAccessible(true);
                lockedField.set(user, false);
            } catch (Exception ignore) {}

            try {
                var failCountField = User.class.getDeclaredField("failCount");
                failCountField.setAccessible(true);
                failCountField.set(user, 0);
            } catch (Exception ignore) {}

            userRepository.save(user);


            int used = verifyEmailTokenRepository.markUsedByToken(req.getEmailVerificationToken());
            if (used != 1) {
                throw new IllegalStateException("검증 토큰 소진 실패");
            }

            VerifyEmailToken fresh = verifyEmailTokenRepository.findByToken(req.getEmailVerificationToken())
                    .orElseThrow(() -> new IllegalStateException("토큰 재조회 실패"));

            fresh.setUser(user);
            try {
                var preSignupField = VerifyEmailToken.class.getDeclaredField("preSignup");
                preSignupField.setAccessible(true);
                preSignupField.set(fresh, false);
            } catch (Exception ignored) {}

            return new SignupResponse(user.getId(), "회원가입이 완료되었습니다. ");
        }

        /**
         * 로그인 흐름
         * 1) 이메일로 사용자 조회
         * 2) locked / status 검사(간단) — 지금은 이메일 인증 안 붙였으므로 생략 가능
         * 3) 비밀번호 일치 검사:
         *    - 실패: failCount+1, 5회 이상이면 locked=true → 예외
         *    - 성공: failCount=0, lastLoginAt=now
         * 4) AccessToken/RefreshToken 발급
         * 5) RefreshToken 영속화 (만료일 + revoked=false)
         */
        @Override
        public LoginResponse login(LoginRequest req) {
            String email = req.getEmail().trim().toLowerCase();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

            // (선택) 상태/잠금 간단 체크
            if (getBoolean(user, "locked")) {
                throw new IllegalArgumentException("계정이 잠금 상태입니다. 관리자에게 문의하세요.");
            }

            if (!"ACTIVE".equals(user.getStatus().name())){
                throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
            }

            String raw = req.getPassword();
            String hash = user.getPasswordHash();
            if (!passwordEncoder.matches(raw, hash)) {
                // 실패 누적
                int fc = getInt(user, "failCount") + 1;
                setInt(user, "failCount", fc);
                if (fc >= 5) {
                    setBoolean(user, "locked", true);
                    setDateTime(user, "lockedAt", LocalDateTime.now());
                }
                throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
            }

            // 성공 처리
            setInt(user, "failCount", 0);
            setDateTime(user, "lastLoginAt", LocalDateTime.now());

            // 역할(roles) 없는 프로젝트면 빈 리스트 전달
            List<String> roles = List.of("USER");

            String access = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), roles);
            String refresh = jwtTokenProvider.createRefreshToken(user.getId());

            RefreshToken rt = new RefreshToken();
            rt.setToken(refresh);
            rt.setUser(user);
            rt.setExpiresAt(LocalDateTime.now().plusDays(14));
            rt.setRevoked(false);
            refreshTokenRepository.save(rt);

            return new LoginResponse(access, refresh);
        }

        /**
         * 로그아웃 흐름
         * - 전달된 RefreshToken을 찾아 revoked=true 로 전환
         * - 토큰이 DB에 없으면 조용히 종료(보안상 동일 응답 유지)
         */
        @Override
        public void logout(LogoutRequest req) {
            refreshTokenRepository.findByToken(req.getRefreshToken())
                    .ifPresent(rt -> rt.setRevoked(true));
        }

    @Override
    public void verifyEmail(VerifyEmailRequest request){
        var token = verifyEmailTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 인증 토큰입니다."));

        // ✅ pre-signup 토큰은 여기서 금지 (가입 요청에 포함해야 함)
        try {
            var preField = VerifyEmailToken.class.getDeclaredField("preSignup");
            preField.setAccessible(true);
            // Object 리턴이므로 안전하게 Boolean 처리
            boolean pre = Boolean.TRUE.equals(preField.get(token));
            if (pre) {
                throw new IllegalArgumentException("이 토큰은 '회원가입 전 검증'용입니다. 회원가입 요청에 포함해 주세요.");
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            // 엔티티 구조가 달라졌거나 접근 실패 시엔 그냥 가드 건너뜀 (겸용 환경에서만)
        }

        if (token.isUsed()) throw new IllegalArgumentException("이미 사용된 인증 토큰입니다.");
        if (token.getExpiresAt() == null || token.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("인증 토큰이 만료되었습니다.");

        var user = token.getUser();
        if (user == null) throw new IllegalStateException("이 토큰에는 연결된 사용자가 없습니다.");

        try{
            var statusField = User.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(user, Enum.valueOf((Class<Enum>) statusField.getType(), "ACTIVE"));
        }catch (Exception ignored){}

        token.setUsed(true);
    }


    /* ---- private helper (리플렉션으로 선택 필드 접근) ---- */
        private boolean getBoolean(Object o, String field) {
            try { var f = o.getClass().getDeclaredField(field); f.setAccessible(true); return (boolean)f.get(o); }
            catch (Exception e) { return false; }
        }
        private void setBoolean(Object o, String field, boolean v) {
            try { var f = o.getClass().getDeclaredField(field); f.setAccessible(true); f.set(o, v); }
            catch (Exception ignored) {}
        }
        private int getInt(Object o, String field) {
            try { var f = o.getClass().getDeclaredField(field); f.setAccessible(true); return (int)f.get(o); }
            catch (Exception e) { return 0; }
        }
        private void setInt(Object o, String field, int v) {
            try { var f = o.getClass().getDeclaredField(field); f.setAccessible(true); f.set(o, v); }
            catch (Exception ignored) {}
        }
        private void setDateTime(Object o, String field, LocalDateTime v) {
            try { var f = o.getClass().getDeclaredField(field); f.setAccessible(true); f.set(o, v); }
            catch (Exception ignored) {}
        }
}
