package allteran.voyage.service;

import allteran.voyage.domain.Ticket;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelService {
    private static final String XLSX_TEMPLATE_DIR = "src" + File.separator + "main" + File.separator + "resources" + File.separator +"static"+File.separator +"files" + File.separator;
    private static final String DAILY_REPORT_TEMPLATE_NAME = "KASSOVIY_OTCHET.xlsx";

    public ByteArrayInputStream generateDailyReport(@NotNull List<Ticket> tickets) throws IOException {
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

        String subheader = dateFormatter.format(LocalDate.now()) + " НОРД-АВИА  МЕРИДИАН  авиабилеты";

        mainSheet.getRow(1).getCell(0).setCellValue(subheader);

        return null;

    }
}
