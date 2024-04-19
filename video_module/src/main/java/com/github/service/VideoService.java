package com.github.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
@Service
public class VideoService {
    @Async
    public ResponseBodyEmitter streamVideo() {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        try{
            Resource videoResource = new ClassPathResource("static/C0170.mp4");
            //ResponseBodyEmitter emitter = new ResponseBodyEmitter();
            InputStream inputStream = new BufferedInputStream(videoResource.getInputStream());
            byte[] buffer = new byte[1024]; //1kb씩
            int bytesRead;
            /**
             InputStream.read(byte[] b, int off, int len) :
             1. byte[] b: 읽은 데이터를 저장할 바이트 배열.
             2. int off: 배열 b에서 데이터 쓰기를 시작할 인덱스]. 시작 offset
             3. int len: 한 번에 읽을 수 있는 최대 바이트 수. 실제로 읽을 수 있는 바이트 수는 이 값과 스트림에 남아있는 데이터 양 중 더 작은 값.
             */
            while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
                // 읽은 데이터만큼의 배열을 복사하여 전송합니다. 이 방법은 버퍼에 남아 있는 잔여 데이터가 전송되는 것을 방지합니다.
                // Here, send a copy of the read bytes to ensure correct data is sent
                byte[] copy = new byte[bytesRead];
                System.arraycopy(buffer, 0, copy, 0, bytesRead);
                emitter.send(buffer, MediaType.APPLICATION_OCTET_STREAM);
            }
        } catch (IOException e) {
            emitter.completeWithError(e);
        } finally {
            try {
                emitter.complete(); // 스트리밍 완료
            } catch (Exception e) {
                // 로깅 필요
                log.error(e.toString());
            }
        }
        return emitter;
    }
}
