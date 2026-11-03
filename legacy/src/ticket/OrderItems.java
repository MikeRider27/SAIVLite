/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticket;

/**
 *
 * @author RojeruSan
 */
public class OrderItems {

    char[] temp = new char[]{' '};

    public OrderItems(char delimit) {
        temp = new char[]{delimit};
    }

    public String GetItemCantidad(String orderItem) {
        String[] delimitado = orderItem.split("" + temp);
        return delimitado[0];
    }

    public String GetItemNombre(String orderItem) {
        String[] delimitado = orderItem.split("" + temp);
        return delimitado[1];
    }

    public String GetItemPrecio(String orderItem) {
        String[] delimitado = orderItem.split("" + temp);
        return delimitado[2];
    }

    public String GeneraItem(String item, String cantidad, String price, String importe) {
        return item + temp[0] + cantidad + temp[0] + price + temp[0] + importe;
    }
}
