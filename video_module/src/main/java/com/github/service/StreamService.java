//package com.github.service;
//
//import com.github.domain.Video;
//import jakarta.annotation.PreDestroy;
//import lombok.AllArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.MediaType;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
//
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Service
//public class StreamService {
//
//    //선언과 동시에 이를 초기화, 클래스의 다른 부분에서 스레드 생성 로직을 신경 쓸 필요가 없도록
//    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
//    private final ViewService viewService;
//
//    public StreamService(final ViewService viewService) {
//        this.viewService = viewService;
//    }
//
//    public ResponseBodyEmitter streamVideoWithExecutor(final int videoId){
//        final Video video = viewService.findVideoById(videoId);
//
//        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
//        executorService.execute(() -> {
//            try {
//                final String resourcePath = "static/" + video.getPath();
//                final Resource videoResource = new ClassPathResource(resourcePath);
//                // try-with-resources 문법으로 감싸서 자동으로 자원을 해제할 수 있도록
//                try (InputStream inputStream = new BufferedInputStream(videoResource.getInputStream())) {
//                    byte[] buffer = new byte[1024];
//                    int bytesRead;
//                    /*
//                     InputStream.read(byte[] b, int off, int len) :
//                     1. byte[] b: 읽은 데이터를 저장할 바이트 배열.
//                     2. int off: 배열 b에서 데이터 쓰기를 시작할 인덱스]. 시작 offset
//                     3. int len: 한 번에 읽을 수 있는 최대 바이트 수. 실제로 읽을 수 있는 바이트 수는 이 값과 스트림에 남아있는 데이터 양 중 더 작은 값.
//                     */
//                    while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
//                        // 읽은 데이터만큼의 배열을 복사하여 전송합니다. 이 방법은 버퍼에 남아 있는 잔여 데이터가 전송되는 것을 방지합니다.
//                        byte[] copy = new byte[bytesRead];
//                        System.arraycopy(buffer, 0, copy, 0, bytesRead);
//                        emitter.send(copy, MediaType.APPLICATION_OCTET_STREAM);
//                    }
//                }
//            } catch (IOException e) {
//                log.error("Error streaming video", e);
//                emitter.completeWithError(e);
//            } finally {
//                try {
//                    emitter.complete();
//                } catch (Exception e) {
//                    log.error("Error completing video stream", e);
//                }
//            }
//        });
//        return emitter;
//    }
//
//    /**
//     PreDestroy: 이 어노테이션이 붙은 메서드는 빈이 파괴되기 전에 자동 호출. 이를 통해 스레드 풀의 shutdown을 시작.
//     shutdown: 현재 처리 중인 작업은 완료하되, 새 작업은 수락하지 않습니다.
//     awaitTermination: shutdown 호출 후, 스레드 풀이 종료될 때까지 대기. 지정된 시간 내에 종료되지 않으면 shutdownNow를 호출하여 현재 진행 중인 모든 작업을 중단하고 스레드 풀을 즉시 종료합니다.
//     shutdownNow: 모든 활동을 즉시 중단하고 스레드 풀을 종료합니다.
//     InterruptedException: 대기 중 인터럽트가 발생한 경우, 즉시 스레드 풀을 종료하고 현재 스레드의 인터럽트 상태를 설정합니다.
//     */
//    @PreDestroy
//    public void destroy() {
//        // 스레드 풀 종료
//        executorService.shutdown();
//        try {
//            //60초마다 스레드 풀 종료 여부 확인
//            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
//                executorService.shutdownNow();
//                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
//                    log.error("Executor service did not terminate");
//                }
//            }
//            } catch (InterruptedException e) {
//            //InterruptedException 처리: 대기 중에 스레드가 인터럽트되면 shutdownNow()를 호출하여 스레드 풀을 즉시 종료,
//            // 해당 스레드의 인터럽트 상태를 다시 설정. 안전하게 리소스를 해제하고 애플리케이션을 정리할 수 있습니다.
//            Thread.currentThread().interrupt();
//            executorService.shutdownNow();
//            log.error("Executor service was interrupted during shutdown ", e);
//        }
//    }
//}
