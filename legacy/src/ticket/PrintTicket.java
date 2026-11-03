/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticket;

import java.util.ArrayList;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

/**
 *
 * @author RojeruSan
 */
public class PrintTicket {

    static ArrayList<String> CabezaLineas = new ArrayList<String>();
    static ArrayList<String> subCabezaLineas = new ArrayList<String>();
    static ArrayList<String> items = new ArrayList<String>();
    static ArrayList<String> totales = new ArrayList<String>();
    static ArrayList<String> LineasPie = new ArrayList<String>();

    public static void AddCabecera(String line) {
        CabezaLineas.add(line);
    }

    public static void AddSubCabecera(String line) {
        subCabezaLineas.add(line);
    }

    public static void AddItem(String item, String cantidad, String price, String importe) {
        OrderItems newItem = new OrderItems(' ');
        items.add(newItem.GeneraItem(item, cantidad, price, importe));
    }

    public static void AddTotal(String price) {
        totales.add(price);
    }

    public static void AddPieLinea(String line) {
        LineasPie.add(line);
    }

    public static String DibujarLinea(int valor) {
        String raya = "";
        for (int x = 0; x < valor; x++) {
            raya += "=";
        }
        return raya;
    }

    public static String DarEspacio() {
        return "\n";
    }

    public static void ImprimirDocumento() {
        String cadena = "";
        for (int cabecera = 0; cabecera < CabezaLineas.size(); cabecera++) {
            cadena += CabezaLineas.get(cabecera);
        }
        for (int subcabecera = 0; subcabecera < subCabezaLineas.size(); subcabecera++) {
            cadena += subCabezaLineas.get(subcabecera);
        }
        for (int ITEM = 0; ITEM < items.size(); ITEM++) {
            cadena += items.get(ITEM);
        }
        for (int total = 0; total < totales.size(); total++) {
            cadena += totales.get(total);
        }
        for (int pie = 0; pie < LineasPie.size(); pie++) {
            cadena += LineasPie.get(pie);
        }

        System.out.println(cadena);

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        DocPrintJob pj = service.createPrintJob();
        byte[] bytes = cadena.getBytes();
        Doc doc = new SimpleDoc(bytes, flavor, null);
        try {
            pj.print(doc, null);
        } catch (Exception e) {
        }
    }

//    public static void main(String[] args) {
//        Date date = new Date();
//        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
//        SimpleDateFormat hora = new SimpleDateFormat("hh:mm:ss aa");
//        Ticket ticket = new Ticket();
//        PrintTicket.AddCabecera("FARMACIAS AHORRA MÁS");
//        PrintTicket.AddCabecera(PrintTicket.DarEspacio());
//        PrintTicket.AddCabecera(PrintTicket.DarEspacio());
//        PrintTicket.AddCabecera("AV. ÁLVARO OBREGÓN");
//        PrintTicket.AddCabecera(PrintTicket.DarEspacio());
//        PrintTicket.AddCabecera("CÓDIGO POSTAL: 60840");
//        PrintTicket.AddCabecera(PrintTicket.DarEspacio());
//        PrintTicket.AddCabecera(PrintTicket.DibujarLinea(29));
//        PrintTicket.AddCabecera(PrintTicket.DarEspacio());
//        PrintTicket.AddCabecera("COALCOMÁN, MICHOACÁN");
//        PrintTicket.AddCabecera(PrintTicket.DarEspacio());
//        PrintTicket.AddCabecera("RFC: 4RGDFGDF54454DFG");
//        PrintTicket.AddCabecera(PrintTicket.DarEspacio());
//        PrintTicket.AddSubCabecera(PrintTicket.DarEspacio());
//        PrintTicket.AddSubCabecera("LE ATENDIO: ROGELIO URIETA");
//        PrintTicket.AddSubCabecera(PrintTicket.DarEspacio());
//        PrintTicket.AddSubCabecera("" + fecha.format(date) + " " + hora.format(date));
//        PrintTicket.AddSubCabecera(PrintTicket.DarEspacio());
//        PrintTicket.AddSubCabecera(PrintTicket.DarEspacio());
//        PrintTicket.AddSubCabecera("ARTICULO CANT PRECIO IMPORTE");
//        PrintTicket.AddSubCabecera(PrintTicket.DarEspacio());
//        PrintTicket.AddSubCabecera(PrintTicket.DibujarLinea(29));
//        PrintTicket.AddSubCabecera(PrintTicket.DarEspacio());
//        PrintTicket.AddItem("Articulo Prueba", "1", "$15.00", "$15");
//        PrintTicket.AddItem("", "", "", PrintTicket.DarEspacio());
//        PrintTicket.AddItem("Articulo Prueba", "2", "$25.00", "$50");
//        PrintTicket.AddItem("", "", "", PrintTicket.DarEspacio());
//        PrintTicket.AddTotal("", PrintTicket.DibujarLinea(28));
//        PrintTicket.AddTotal("", PrintTicket.DarEspacio());
//        PrintTicket.AddTotal("TOTAL", "$65.00");
//        PrintTicket.AddTotal("", PrintTicket.DarEspacio());
//        PrintTicket.AddTotal("", PrintTicket.DarEspacio());
//        PrintTicket.AddPieLinea(PrintTicket.DibujarLinea(29));
//        PrintTicket.AddPieLinea(PrintTicket.DarEspacio());
//        PrintTicket.AddPieLinea("MÁS CALIDAD Y MEJOR PRECIO");
//        PrintTicket.AddPieLinea(PrintTicket.DarEspacio());
//        PrintTicket.AddPieLinea("VIVE LA EXPERIENCIA EN xxx");
//        PrintTicket.AddPieLinea(PrintTicket.DarEspacio());
//        PrintTicket.AddPieLinea("Gracias por su compra");
//        PrintTicket.AddPieLinea(PrintTicket.DarEspacio());
//        PrintTicket.ImprimirDocumento();
//    }
}
