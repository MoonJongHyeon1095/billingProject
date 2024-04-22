package com.github.service;

import com.github.exception.AdErrorCode;
import com.github.exception.AdException;
import com.github.mapper.AdvertisementDetailMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class AdvertisementService {
    private final AdvertisementDetailMapper advertisementDetailMapper;

    @Transactional
    public void countAdView( final int videoId, final int adViewCount) {
        //해당 videoId의 영상에 5분 간격으로 광고가 몇개 붙어있는지
        List<Integer> adList = getAllAdByVideoId(videoId);
        int plusViewCount=1;
        int remainingViewCount=0;
        int attachedAdNum = adList.size();

        if(adViewCount==0 || attachedAdNum==0) throw new AdException(AdErrorCode.AD_NOT_FOUND);

        if(adViewCount > attachedAdNum ){
            //1시간 짜리 영상에 12개의 광고가 있고, adViewCount가 27회라면 일단 모든 광고 조회수 +2
            //이후 나머지만큼 오름차순 정렬된 adPriority(광고 우선순위) 순회하며 조회수 +1
            plusViewCount = adViewCount/ attachedAdNum;
            remainingViewCount = adViewCount % attachedAdNum;
            //1. 1시간 짜리 영상에 12개의 광고가 있고, adViewCount가 27회라면 일단 모든 광고 조회수 +2
            for(int adPriority : adList){
                updateViewCountByPriority(adPriority, plusViewCount);
            }
            //2. 이후 나머지만큼 오름차순 정렬된 adPriority(광고 우선순위) 순회하며 조회수 +1
            if(remainingViewCount > 0) {
                for(int i=0; i<remainingViewCount; i++){
                    updateViewCountByPriority( adList.get(i), 1);
                }
            }
        }else{
            //adViewCount가 1이면, 광고들 중 우선순위 높은 1개만 재생
            for(int i=0; i<adViewCount; i++){
                updateViewCountByPriority( adList.get(i), 1);
            }
        }

    }

    private void updateViewCountByPriority(final int adPriority, final int plusView){
        advertisementDetailMapper.updateViewCountByPriority(adPriority, plusView);
    }

    private Integer getCountByVideoId(final int videoId) {
        return advertisementDetailMapper.countByVideoId(videoId);
    }

    private List<Integer> getAllAdByVideoId(final int videoId){
        return advertisementDetailMapper.findAllByVideoId(videoId);
    }
}
