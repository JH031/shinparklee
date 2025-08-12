package spl.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spl.demo.entity.NewsEntity;
import spl.demo.entity.SignupEntity;
import spl.demo.security.CustomUserDetails;
import spl.demo.service.ScrapService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scrap")
public class ScrapController {

    private final ScrapService scrapService;

    @PostMapping("/{newsId}")
    @Operation(summary = "뉴스 스크랩 (로그인 사용자만 가능)")
    public ResponseEntity<String> scrapNews(
            @PathVariable("newsId") String newsId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        SignupEntity user = userDetails.getUser();
        scrapService.scrapNews(user.getId(), newsId);
        return ResponseEntity.ok("스크랩 완료");
    }

    @GetMapping
    @Operation(summary = "스크랩한 뉴스 목록 조회")
    public ResponseEntity<?> getScrappedNews(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        SignupEntity user = userDetails.getUser();
        List<NewsEntity> scrappedNews = scrapService.getScrappedNews(user.getId());
        return ResponseEntity.ok(scrappedNews);
    }

    @DeleteMapping("/{newsId}")
    @Operation(summary = "스크랩 취소")
    public ResponseEntity<String> cancelScrap(
            @PathVariable("newsId") String newsId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        SignupEntity user = userDetails.getUser();
        scrapService.cancelScrap(user.getId(), newsId);
        return ResponseEntity.ok("스크랩 취소 완료");
    }

    @GetMapping("/list")
    @Operation(summary = "스크랩한 뉴스의 기본말투 목록")
    public ResponseEntity<?> getScrapWithSummaries(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        SignupEntity user = userDetails.getUser();
        return ResponseEntity.ok(scrapService.getScrappedNewsWithSummaries(user.getId()));
    }
    @GetMapping("/style/list")
    @Operation(summary = "스크랩한 뉴스의 스타일 요약 목록 조회")
    public ResponseEntity<?> getStyleSummariesOnly(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        SignupEntity user = userDetails.getUser();
        return ResponseEntity.ok(scrapService.getStyleSummariesOnly(user.getId()));
    }
    @GetMapping("/mypage")
    @Operation(summary = "유저 정보 조회")
    public ResponseEntity<?> getMyPageInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        SignupEntity user = userDetails.getUser();
        return ResponseEntity.ok(scrapService.getMyPage(user.getId()));
    }
    @GetMapping("/mypage/grouped")
    @Operation(summary = "마이페이지: 카테고리별 스크랩 뉴스 목록")
    public ResponseEntity<?> getGroupedScraps(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        return ResponseEntity.ok(scrapService.getScrapsGroupedByCategory(userDetails.getUser().getId()));
    }
}

