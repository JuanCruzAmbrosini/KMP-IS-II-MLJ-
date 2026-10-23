package ingsoftware.zeroshop.service.report;

import com.lowagie.text.DocumentException;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import ingsoftware.zeroshop.entity.transaction.Invoice;
import ingsoftware.zeroshop.entity.transaction.InvoiceDetail;
import ingsoftware.zeroshop.entity.transaction.OrderDetail;
import ingsoftware.zeroshop.entity.transaction.SaleOrder;

@Service
public class InvoicePdfService {

    private final TemplateEngine templateEngine;

    public InvoicePdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generate(SaleOrder order, Invoice invoice,
                           List<InvoiceDetail> invoiceDetails,
                           List<OrderDetail> orderDetails) {
        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("invoice", invoice);
        context.setVariable("invoiceDetails", invoiceDetails);
        context.setVariable("details", orderDetails);

        String html = templateEngine.process("client/invoice-pdf", context);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(output);
            return output.toByteArray();
        } catch (DocumentException | IOException exception) {
            throw new IllegalStateException("No se pudo generar el PDF de la factura.", exception);
        }
    }
}
