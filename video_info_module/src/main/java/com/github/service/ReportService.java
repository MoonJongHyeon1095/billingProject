package com.github.service;

import com.github.controller.response.UserBillResponse;
import com.github.dto.DailyBillDto;
import com.github.dto.PeriodViewTop5Dto;
import com.github.exception.MailErrorCode;
import com.github.exception.MailException;
import com.github.repository.RedisInfoRepository;
import com.github.util.Serializer;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class ReportService {
    private final JavaMailSender javaMailSender;
    private final StatisticService statisticService;
    private final BillingService billingService;
    private final RedisInfoRepository redisInfoRepository;
    @Value("${mail.test}")
    private final String test;

    public ReportService(JavaMailSender javaMailSender,
                         StatisticService statisticService,
                         BillingService billingService,
                         RedisInfoRepository redisInfoRepository,
                         @Value("${mail.test}") String test) {
        this.javaMailSender = javaMailSender;
        this.statisticService = statisticService;
        this.billingService = billingService;
        this.redisInfoRepository = redisInfoRepository;
        this.test = test;
    }

    public void sendEmailWithAttachment(String email) {
        UserBillResponse bill = billingService.findWeeklyBillingByEmail(email);
        String path = generatePdfPath(email);
        File file = new File(path);
        if (!file.exists()) {
            createPdf(path, bill.getVideoStatisticList());
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 수신자 설정
            String[] toArr = new String[1];
            toArr[0] = test;
            helper.setTo(toArr);

            // 제목 설정
            helper.setSubject("weekly report");

            // 본문 설정
            helper.setText("Hello!!!!");

            // 첨부 파일 추가
            FileSystemResource fileResource = new FileSystemResource(file);
            helper.addAttachment(Objects.requireNonNull(fileResource.getFilename()), fileResource);

            // 이메일 전송
            javaMailSender.send(message);
        }catch(Exception e){
            e.printStackTrace();
            throw new MailException(MailErrorCode.SEND_MAIN_FAILED);
        }
    }

    protected void createPdf(String path, List<DailyBillDto> billList){
        List<PeriodViewTop5Dto> weeklyTop5;
        try{
            Optional<String> result = redisInfoRepository.getByKey("weeklyViewCount");
            if(result.isEmpty()){
                weeklyTop5 = statisticService.getWeeklyTop5WithTitle();
                redisInfoRepository.save("weeklyViewCount", Serializer.serializeListToJson(weeklyTop5), 86400);
            }else{
               weeklyTop5 = Serializer.deserializeJsonToList(result.get());
            }
        }catch(IOException e){
            throw new RuntimeException("Error processing JSON data", e);
        }

        // try-with-resources를 사용하여 자동 리소스 관리
        try (PdfWriter writer = new PdfWriter(path);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf);
        ) {

            document.add(new Paragraph("weekly view Top 5"));
            document.add(new Paragraph("\n")); // 여백 추가
            Table table = new Table(3);
            table.addCell(new Cell().add(new Paragraph("video ID")));
            table.addCell(new Cell().add(new Paragraph("title")));
            table.addCell(new Cell().add(new Paragraph("weeklyViewCount")));
            // 데이터를 테이블에 추가
            for (PeriodViewTop5Dto dto : weeklyTop5) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(dto.getVideoId()))));
                table.addCell(new Cell().add(new Paragraph(dto.getTitle())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(dto.getTotalViewCount()))));
            }
            // 문서에 테이블 추가
            document.add(table);
            document.add(new Paragraph("\n")); // 여백 추가

            document.add(new Paragraph("weekly billing"));
            document.add(new Paragraph("\n")); // 여백 추가
            Table table2 = new Table(4);
            table2.addCell(new Cell().add(new Paragraph("video ID")));
            table2.addCell(new Cell().add(new Paragraph("video profit")));
            table2.addCell(new Cell().add(new Paragraph("ad profit")));
            table2.addCell(new Cell().add(new Paragraph("bill createdAt")));

            for (DailyBillDto dto : billList) {
                table2.addCell(new Cell().add(new Paragraph(String.valueOf(dto.getVideoId()))));
                table2.addCell(new Cell().add(new Paragraph(String.valueOf(dto.getDailyVideoProfit()))));
                table2.addCell(new Cell().add(new Paragraph(String.valueOf(dto.getDailyAdProfit()))));
                table2.addCell(new Cell().add(new Paragraph(String.valueOf(dto.getCreatedAt()))));
            }
            document.add(table2);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("파일 경로가 잘못되었습니다.", e);
        } catch (Exception e) {
            throw new MailException(MailErrorCode.CREATE_PDF_FAILED);
        }
    }

    private String generatePdfPath(final String email) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        return "./pdf/Top5WeeklyReport_" + today + email + ".pdf";
    }
}
