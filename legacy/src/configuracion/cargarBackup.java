/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package configuracion;

import javax.swing.JProgressBar;

/**
 *
 * @author IsraelCM
 */
public class cargarBackup extends Thread{
    JProgressBar progreso;
    public cargarBackup(JProgressBar progreso){
        super();
        this.progreso=progreso;
    }
    @Override
    public void run(){
        for(int i=1;i<=100;i++){
            progreso.setValue(i);
            pausa(30);
        }
    }
    public void pausa(int mlSeg){
        try{
            Thread.sleep(mlSeg);
        }
        catch(Exception e){
            
        }
    }
}
