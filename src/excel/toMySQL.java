/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import alertas.ErrorAlert;
import almacen.Operaciones;
import almacen.Sentencias;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JFrame;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.hssf.usermodel.HSSFSheet;

/**
 *
 * @author RojeruSan
 */
public class toMySQL {

    public static String importarTabla(String archivo) {

        int contador = 0;
        try {
            FileInputStream input = new FileInputStream(archivo);
            POIFSFileSystem fs = new POIFSFileSystem(input);
            HSSFWorkbook wb = new HSSFWorkbook(fs);

            HSSFSheet sheet = wb.getSheetAt(0);
            Row row;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                row = sheet.getRow(i);
                String categoria = row.getCell(0).getStringCellValue();
                String codigo = row.getCell(1).getStringCellValue();
                String descripcion = row.getCell(2).getStringCellValue();
                String precio = row.getCell(3).getStringCellValue();
                String precio_venta = row.getCell(4).getStringCellValue();
                String existencias = row.getCell(5).getStringCellValue();
                String stock_min = row.getCell(6).getStringCellValue();
                String se_venden_en = row.getCell(7).getStringCellValue();
                String ubicacion = row.getCell(8).getStringCellValue();

                if (!categoria.equals("-Sin categoría-")) {
                    if (!Operaciones.isExisteCat(categoria)) {
                        if (isRegistrado(categoria)) {
                            System.out.println("Categoría "+categoria+" registrada");
                        }
                    }
                }
                
                Sentencias s = new Sentencias();
                int id = Operaciones.extraerIDMax();

                s.setId(id);
                s.setCategoria(categoria);
                s.setCodigo(codigo);
                s.setDescripcion(descripcion);
                s.setPrecio(Double.parseDouble(precio));
                s.setPrecio_venta(Double.parseDouble(precio_venta));

                if (existencias.equals("NO UTILIZA")) {
                    s.setStock_min(0);
                    s.setExistencias(0);
                    s.setInventario_utiliza(false);
                } else {
                    s.setStock_min(Integer.parseInt(stock_min));
                    s.setExistencias(Integer.parseInt(existencias));
                    s.setInventario_utiliza(true);
                }
                s.setSe_venden_en(se_venden_en);
                s.setUbicacion(ubicacion);

                if (almacen.Operaciones.isExiste(codigo)) {
                    contador++;
                } else {
                    if (Operaciones.isRegistrado(s)) {
                        System.out.println("Import rows " + i);
                    }
                }

            }
            input.close();
            System.out.println("Success import excel to mysql table");
            if (contador != 0) {
                Operaciones.setListar("");
                almacen.pnlAlmacen.c.dispose();
                ErrorAlert e = new ErrorAlert(new JFrame(), true);
                e.msj1.setText("Algo salio mal.");
                e.msj2.setText(contador + " registros de " + sheet.getLastRowNum() + " no fuerón");
                e.msj3.setText("importados ya que su código ya se encuentra registrado.");
                e.setVisible(true);

                return "existe";
            }

            return "hecho";
        } catch (IOException | IllegalStateException ex) {
            return "error";
        }
    }

    private static boolean isRegistrado(String categoria) {
        Sentencias s = new Sentencias();

        int id = Operaciones.extraerIDMaxCat();
        s.setIdCategoria(id);
        s.setCategoria(categoria);

        if (Operaciones.isRegistradoCat(s)) {
            return true;
        } else {
            return false;
        }
    }
}
