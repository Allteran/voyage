package allteran.voyage.service;

import allteran.voyage.domain.PayType;
import allteran.voyage.domain.Ticket;
import allteran.voyage.domain.TicketType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelService {
    @Value("${paytype.id.card}")
    private String PAYTYPE_ID_CARD;
    @Value("${paytype.id.cash}")
    private String PAYTYPE_ID_CASH;

    private final PayTypeService payTypeService;

    private static final String XLSX_TEMPLATE_DIR = "src" + File.separator + "main" + File.separator + "resources" + File.separator +"static"+File.separator +"files" + File.separator;
    private static final String DAILY_REPORT_TEMPLATE_NAME = "KASSOVIY_OTCHET.xls";

    @Autowired
    public ExcelService(PayTypeService payTypeService) {
        this.payTypeService = payTypeService;
    }

    //TODO: TEST THIS MF
    public ByteArrayInputStream generateDailyReport(@NotNull List<Ticket> tickets, LocalDate reportDate) throws IOException {
        File currentDir = new File(".");
        String path = currentDir.getAbsolutePath().substring(0, currentDir.getAbsolutePath().length() - 1) +
                XLSX_TEMPLATE_DIR + DAILY_REPORT_TEMPLATE_NAME;
        FileInputStream file = null;
        Workbook workbook = null;
        try {
            file = new FileInputStream(path);
            workbook = new XSSFWorkbook(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert workbook != null;
        Sheet mainSheet = workbook.getSheetAt(0);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        String subheader = dateFormatter.format(reportDate) + " НОРД-АВИА  МЕРИДИАН  авиабилеты";
        mainSheet.getRow(1).getCell(0).setCellValue(subheader);

        //prepare some font color modifications for subheader or regular text
        Font typeHeaderFont = workbook.createFont();
        typeHeaderFont.setFontName("Arial Cyr");
        typeHeaderFont.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        typeHeaderFont.setBold(true);

        TicketType type = new TicketType();
        int row = 5;
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            if(!tickets.get(i).getType().equals(type)) {
                //describe case for situation when we have to write type name first
                type = ticket.getType();
                Cell typeCell = mainSheet.getRow(row).getCell(4);
                //setup type style to subheader
                CellStyle style = workbook.createCellStyle();
                style.setFont(typeHeaderFont);
                style.setAlignment(HorizontalAlignment.CENTER);
                typeCell.setCellStyle(style);
                //now we can set value
                typeCell.setCellValue(ticket.getType().getName());
                row ++;
            }
            //now set ticket number to cell
            mainSheet.getRow(row).getCell(2).setCellValue(ticket.getTicketNumber());
            //set issue date to cell
            mainSheet.getRow(row).getCell(3).setCellValue(dateFormatter.format(ticket.getIssueDate()));
            //set route
            mainSheet.getRow(row).getCell(4).setCellValue(ticket.getFlightRoute());
            //set tariff
            mainSheet.getRow(row).getCell(5).setCellValue(ticket.getTariffPrice());
            //set tax YQ
            mainSheet.getRow(row).getCell(6).setCellValue(ticket.getTaxYQPrice());
            //set tax RU/YR
            mainSheet.getRow(row).getCell(7).setCellValue(ticket.getTaxRUYRPrice());

            //set total price depends on pay type (card or cash)
            PayType cardPayType = payTypeService.findById(Long.valueOf(PAYTYPE_ID_CARD), new PayType());
            if(ticket.getPayType().equals(cardPayType)) {
                mainSheet.getRow(row).getCell(8).setCellValue(ticket.getTotalPrice());
            } else {
                mainSheet.getRow(row).getCell(9).setCellValue(ticket.getTotalPrice());
            }
            //set comment
            mainSheet.getRow(row).getCell(11).setCellValue(ticket.getComment());
            row++;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);

        return new ByteArrayInputStream(outputStream.toByteArray());

    }
}
