package allteran.voyage.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;

@Service
public class ExcelService {
    private static final String XLSX_TEMPLATE_DIR = "src" + File.separator + "main" + File.separator + "resources" + File.separator +"static"+File.separator +"files" + File.separator;
    private static final String DAILY_REPORT_TEMPLATE_NAME = "KASSOVIY_OTCHET.xlsx";

}
