package groovytest

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import org.apache.commons.lang3.StringUtils

@Grapes([
        @Grab(group = 'org.apache.commons', module = 'commons-lang3', version = '3.4'),
        @Grab(group = 'com.itextpdf', module = 'itextpdf', version = '5.5.6'),
        @Grab(group = 'org.bouncycastle', module = 'bcpkix-jdk15on', version = '1.49'),
        @Grab(group = 'org.bouncycastle', module = 'bcprov-jdk15on', version = '1.49')
])

class FixPDFMetaInfo {
    public static void main(String[] args) {
        def outDir = new File(args[1])
        new File(args[0]).listFiles().sort().each { File inFile ->
            def strings = StringUtils.splitByWholeSeparator(inFile.name, " - ")
            if (strings.length == 3) {
                def author = strings[1]
                def title = strings[0] + ' ' + strings[2].replaceAll("\\s+", " ")
                def outFile = new File(outDir, inFile.name)
                writeMetaToNewFile(inFile, outFile, title, author, "", "", "")
                true
            }
        }
    }

    private
    static void writeMetaToNewFile(File inFile, File outFile, String title, String author, String subject, String keywords, String creator) {
        PdfReader reader = new PdfReader(inFile.absolutePath);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outFile));
        Map<String, String> info = reader.getInfo();
        info.put("Title", title);
        info.put("Subject", subject);
        info.put("Keywords", keywords);
        info.put("Creator", creator);
        info.put("Author", author);
        try {
            println "processing ${inFile}..."
            stamper.setMoreInfo(info);
        } finally {
            stamper.close();
            reader.close();
        }
    }
}
