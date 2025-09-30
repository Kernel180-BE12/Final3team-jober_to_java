package com.example.final_projects.controller;

import com.example.final_projects.dto.ApiResult;
import com.example.final_projects.dto.auth.*;
import com.example.final_projects.service.AuthServiceImpl;
import com.example.final_projects.service.TokenServiceImpl;
import com.example.final_projects.support.RefreshCookieFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthServiceImpl authService;
    private final TokenServiceImpl tokenService;
    private final RefreshCookieFactory cookieFactory;

    public AuthController(AuthServiceImpl authService,
                          TokenServiceImpl tokenService,
                          RefreshCookieFactory cookieFactory) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.cookieFactory = cookieFactory;
    }

    @PostMapping("/signup")
    public ApiResult<SignupResponse> signup(@Valid @RequestBody SignupRequest req){
        return ApiResult.ok(authService.signup(req));
    }

    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@Valid @RequestBody LoginRequest req){
        return ApiResult.ok(authService.login(req));
    }

    /**
     * 로그아웃(단일 엔드포인트): 쿠키 우선 + 바디 백업(하위호환)
     * - 서버 DB의 RT 무효화 (토큰 있으면)
     * - 브라우저 RT 쿠키 즉시 만료(SET-COOKIE)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResult<Void>> logout(
            @RequestBody(required = false) LogoutRequest req,
            HttpServletRequest httpReq
    ){
        // 1) 쿠키에서 refresh 우선 읽기
        String rt = cookieFactory.read(httpReq);

        // 2) 바디 백업(구클라이언트 호환)
        if ((rt == null || rt.isBlank()) && req != null) {
            rt = req.getRefreshToken();
        }

        // 3) 있으면 서버측 무효화
        if (rt != null && !rt.isBlank()) {
            tokenService.deleteRefreshToken(rt);
        }

        // 4) 브라우저 쿠키 즉시 만료
        ResponseCookie expire = cookieFactory.buildExpireCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expire.toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(ApiResult.ok("로그아웃 되었습니다.", null));
    }

    @GetMapping("/verify")
    public ApiResult<Void> verify(@RequestParam("token") String token){
        authService.verifyEmail(new VerifyEmailRequest(token));
        return ApiResult.ok("이메일 인증이 완료되었습니다.", null);
    }
}
