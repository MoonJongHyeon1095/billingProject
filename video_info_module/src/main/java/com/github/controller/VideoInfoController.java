package com.github.controller;

import com.github.common.response.Response;
import com.github.controller.response.DailyTopResponse;
import com.github.controller.response.UserBillResponse;
import com.github.service.BillingService;
import com.github.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/info")
public class VideoInfoController {
    private final BillingService billingService;
    private final StatisticService statisticService;

    /**
     * 일간 통계
     * @return Response<DailyTopResponse>  일간 조회수 Top5, 재생시간 Top5 리스트 반환
     */
    @GetMapping("/top5/daily")
    public Response<DailyTopResponse> getDailyTop5(){
        DailyTopResponse response = statisticService.findDailyTop5();
        return Response.success(response);
    }

    @GetMapping("/top5/weekly")
    public Response getWeeklyTop5(){
        return Response.success();
    }
    @GetMapping("/top5/monthly")
    public Response getMonthlyTop5(){

        return Response.success();
    }

    /**
     * 일간 정산
     * 토큰 값에서 이메일을 찾아, 정산 정보를 가져옵니다.
     * @return Response<UserBillResponse> videoId, 해당영상수익, 해당영상 광고수익의 리스트
     */
    @GetMapping("/bill/daily")
    public Response<UserBillResponse> getDailyUserBilling(
            @RequestHeader("X-User-Email") final String email
    ){
        if(email==null) Response.error("로그인 해주세요....");
        UserBillResponse userBillResponse = billingService.findDailyBillingByEmail(email);
         return Response.success(userBillResponse);
    }

    /**
     * 주간 정산
     * 토큰 값에서 이메일을 찾아, 정산 정보를 가져옵니다.
     * @return Response<UserBillResponse> videoId, 해당영상수익, 해당영상 광고수익의 리스트
     */
    @GetMapping("/bill/weekly")
    public Response<UserBillResponse> getWeeklyUserBilling(@RequestHeader("X-User-Email") final String email){
        if(email==null) Response.error("로그인 해주세요....");
        UserBillResponse response = billingService.findWeeklyBillingByEmail(email);
        return Response.success(response);
    }

    /**
     * 월간 정산
     * 토큰 값에서 이메일을 찾아, 정산 정보를 가져옵니다.
     * @return Response<UserBillResponse> videoId, 해당영상수익, 해당영상 광고수익의 리스트
     */
    @GetMapping("/bill/monthly")
    public Response<UserBillResponse> getMonthlyUserBilling(@RequestHeader("X-User-Email") final String email){
        if(email==null) Response.error("로그인 해주세요....");
        UserBillResponse response = billingService.findMonthlyBillingByEmail(email);
        return Response.success(response);
    }


}
