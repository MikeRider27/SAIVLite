/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package actualizar;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RojeruSan
 */
public class Actualizacion {

    public String getTextContent(URL url) throws IOException {
        Scanner s = new Scanner(url.openStream()).useDelimiter("\\Z");
        String content = s.next();
        return content;
    }

    public boolean isConexionInternet() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/RojeruSan/Archivos/master/SAIVLite.txt");
            URLConnection conn = url.openConnection();
            conn.connect();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }

    public String getVersion() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/RojeruSan/Archivos/master/SAIVLite.txt");
            URLConnection conn = url.openConnection();
            conn.connect();

            return getTextContent(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return null;
        }
    }

    public static void abrirEnlace(String link) {
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(Actualizacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
