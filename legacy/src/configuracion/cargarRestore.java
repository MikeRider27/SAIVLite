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
public class cargarRestore extends Thread{
    JProgressBar progreso;
    public cargarRestore(JProgressBar progreso){
        super();
        this.progreso=progreso;
    }
    @Override
    public void run(){
        for(int i=1;i<=100;i++){
            progreso.setValue(i);
            pausa(150);
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
