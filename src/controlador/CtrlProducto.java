package controlador;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.output.PrinterOutputStream;
import modelo.Productos;
import modelo.Facturacion;
import vista.IMenu;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import modelo.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class CtrlProducto extends CtrlImprimir implements ActionListener, CaretListener, MouseListener, KeyListener {

	private static CtrlProducto instancia = null;
	private static Productos productosModel;
	private static IMenu menu;
	private static Facturacion factura;
	Date fecha;
	String id;
	DefaultTableModel modelo;
	int permiso;

	private CtrlProducto(Productos productosModel, IMenu menu, int permiso) {
		this.permiso = permiso;
		this.productosModel = productosModel;
		this.menu = menu;
		this.factura = new Facturacion();
		this.fecha = new Date();
		this.menu.btnGuardarProducto.addActionListener(this);
		this.menu.btnActualizarProducto.addActionListener(this);
		this.menu.btnNuevoProducto.addActionListener(this);
		this.menu.btnAgregarCategorias.addActionListener(this);
		this.menu.btnAgregarLaboratorio.addActionListener(this);
		this.menu.btnMostrarStockMinimo.addActionListener(this);
		this.menu.txtBuscarProducto.addCaretListener(this);
		this.menu.txtBuscarPorNombre.addCaretListener(this);
		this.menu.txtBuscarPorCategoria.addCaretListener(this);
		this.menu.txtBuscarPorLaboratorio.addCaretListener(this);
		this.menu.txtBuscarCategoriaAdd.addCaretListener(this);
		this.menu.txtLaboratorioAdd.addCaretListener(this);
		this.menu.txtCodBarraProducto.addCaretListener(this);
		this.menu.EditarProducto.addActionListener(this);
		this.menu.BorrarProducto.addActionListener(this);
		this.menu.AddProductoStock.addActionListener(this);
		this.menu.btnCalcularGanancia.addActionListener(this);
		this.menu.btnAgregarProducto.addActionListener(this);
		this.menu.btnImprimirMinStock.addActionListener(this);
		this.menu.btnBuscarMinStock.addActionListener(this);
		this.menu.rbBuscarCategoria.addActionListener(this);
		this.menu.rbBuscarLaboratorio.addActionListener(this);
		this.menu.rbBuscarNombreCodBarra.addActionListener(this);
		this.menu.kardex.addActionListener(this);
		this.menu.txtCategoriaProducto.addMouseListener(this);
		this.menu.txtLaboratorioProducto.addMouseListener(this);
		this.menu.tblAddCategoria.addMouseListener(this);
		this.menu.tblAddLaboratorio.addMouseListener(this);
		this.menu.AgregarProductoStock.addActionListener(this);
		this.menu.btnGenerarReporteStock.addMouseListener(this);
		this.menu.txtCodBarraProducto.addKeyListener(this);
		this.menu.txtNombreProducto.addKeyListener(this);
		this.menu.spPrecioVenta.addKeyListener(this);
		this.menu.spPrecioCompra.addKeyListener(this);
		this.menu.txtCantidadProducto.addKeyListener(this);
		this.menu.txtCategoriaProducto.addKeyListener(this);
		this.menu.txtLaboratorioProducto.addKeyListener(this);
		this.menu.txtUbicacionProducto.addKeyListener(this);
		this.menu.txtDescripcionProducto.addKeyListener(this);
		this.menu.btnGuardarProducto.addKeyListener(this);
		this.menu.cmbMonedaCompraProducto.addKeyListener(this);
		this.menu.cmbMonedaVentaProducto.addKeyListener(this);
		this.menu.spUtilidad.addKeyListener(this);
		this.id = null;
		this.modelo = new DefaultTableModel();
		iniciar();
	}

	public static void createInstanciaController(IMenu menu, Productos productosModel,int permiso){
		if(instancia == null)
			instancia = new CtrlProducto(productosModel,menu,permiso);
	}
	
	public void iniciar() {
		MostrarProductos("");
		StockMinimoP("", 15);
		MostrarProductosVender("");
		llenarAddCategoria("");
		llenarAddLaboratorio("");
		DeshabilitarProductos();
		menu.rbBuscarNombreCodBarra.setSelected(true);
		this.menu.jcFechaVProducto.setDate(fecha);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == menu.btnGuardarProducto) {
			guardarProducto();
		}
		if (e.getSource() == menu.btnCalcularGanancia) {
			calcularGanancia();
		}
		if (e.getSource() == menu.btnActualizarProducto) {
			actualizarProducto();
		}
		if (e.getSource() == menu.btnNuevoProducto) {
			HabilitarProductos();
			LimpiarProducto();
			menu.txtCodBarraProducto.requestFocus();
		}
		if (e.getSource() == menu.EditarProducto) {
			editarProducto();
		}
		if (e.getSource() == menu.BorrarProducto) {
			borrarProducto();
		}
		if (e.getSource() == menu.AddProductoStock) {
			int filaseleccionada = menu.tblProductos.getSelectedRow();
			String id = "", nombre = "", Cantidad = "";
			try {
				if (filaseleccionada == -1) {
					//JOptionPane.showMessageDialog(null, "Seleccione una Fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
				} else {
					menu.VentanaAddProductoStock.setSize(400, 130);
					menu.VentanaAddProductoStock.setVisible(true);
					menu.VentanaAddProductoStock.setLocationRelativeTo(null);
					this.modelo = (DefaultTableModel) menu.tblProductos.getModel();
					//id = (String) this.modelo.getValueAt(filaseleccionada, 0);
					nombre = (String) this.modelo.getValueAt(filaseleccionada, 2);
					menu.lblNombreProductStock.setText("Cantidad de " + nombre + " a Agregar");
					/*Cantidad = menu.txtCantidadAgregar.getText();//JOptionPane.showInputDialog("Ingrese la Cantidad de " + nombre, "");
                    if(Cantidad!=null)
                    {
                        productosModel.AgregarProductoStock(id, Cantidad);
                        MostrarProductos("");
                        MostrarProductosVender("");
                    }*/
				}
			} catch (Exception err) {
				JOptionPane.showMessageDialog(null, err + "en la funcion add productoStock");
			}
		}
		if (e.getSource() == menu.btnAgregarProducto) {
			try {
				String Cantidad = "", user = this.menu.lblUsuarioSistema.getText();
				int filaseleccionada = menu.tblProductos.getSelectedRow(), producto;
				float c;
				this.modelo = (DefaultTableModel) menu.tblProductos.getModel();
				String id = (String) this.modelo.getValueAt(filaseleccionada, 0);
				producto = Integer.parseInt(id);
				Cantidad = menu.txtCantidadAgregar.getText();//JOptionPane.showInputDialog("Ingrese la Cantidad de " + nombre, "");
				c = Float.parseFloat(Cantidad);
				if (Cantidad != null) {
					productosModel.AgregarProductoStock(id, Cantidad);
					productosModel.guardarKardexIncial(producto, user, c, "add");
					MostrarProductos("");
					MostrarProductosVender("");
					StockMinimoP("", 0);
					inversion();
				}
			} catch (Exception err) {
				//JOptionPane.showMessageDialog(null, err+"en la funcion AgregarProducto");
			}
			menu.VentanaAddProductoStock.setVisible(false);
			menu.txtCantidadAgregar.setText("");
		}
		if (e.getSource() == menu.btnMostrarStockMinimo) {
			menu.StockMinimo.setSize(1118, 495);
			menu.StockMinimo.setLocationRelativeTo(null);
			//StockMinimo.setModal(true);
			menu.StockMinimo.setVisible(true);
			StockMinimoP("", 15);
		}
		if (e.getSource() == menu.btnBuscarMinStock) {
			float cant = 0;
			if (menu.txtCantidadStockM.getText().equals("")) {

			} else {
				cant = Float.parseFloat(menu.txtCantidadStockM.getText());
				StockMinimoP(menu.txtCategoriaStockM.getText(), cant);
			}
		}
		if (e.getSource() == menu.rbBuscarCategoria) {
			if (menu.rbBuscarCategoria.isSelected() == true) {
				menu.txtBuscarPorNombre.setEnabled(false);
				menu.txtBuscarPorCategoria.setEnabled(true);
				menu.txtBuscarPorLaboratorio.setEnabled(false);
				menu.txtBuscarPorLaboratorio.setText("");
				menu.txtBuscarPorNombre.setText("");
				menu.txtBuscarPorCategoria.requestFocus();
			}
		}
		if (e.getSource() == menu.rbBuscarLaboratorio) {
			if (menu.rbBuscarLaboratorio.isSelected() == true) {
				menu.txtBuscarPorNombre.setEnabled(false);
				menu.txtBuscarPorCategoria.setEnabled(false);
				menu.txtBuscarPorLaboratorio.setEnabled(true);
				menu.txtBuscarPorNombre.setText("");
				menu.txtBuscarPorCategoria.setText("");
				menu.txtBuscarPorLaboratorio.requestFocus();
			}
		}
		if (e.getSource() == menu.rbBuscarNombreCodBarra) {
			if (menu.rbBuscarNombreCodBarra.isSelected() == true) {
				menu.txtBuscarPorNombre.setEnabled(true);
				menu.txtBuscarPorCategoria.setEnabled(false);
				menu.txtBuscarPorLaboratorio.setEnabled(false);
				menu.txtBuscarPorCategoria.setText("");
				menu.txtBuscarPorLaboratorio.setText("");
				menu.txtBuscarPorNombre.requestFocus();
			}
		}

		if (e.getSource() == menu.btnImprimirMinStock) {
			ImprimirMinStock();
		}
		if (e.getSource() == menu.kardex) {
			int filaseleccionada = this.menu.tblProductos.getSelectedRow();
			String id, nombre, stock;
			try {
				if (filaseleccionada == -1) {

				} else {
					id = (String) this.menu.tblProductos.getValueAt(filaseleccionada, 0);
					nombre = (String) this.menu.tblProductos.getValueAt(filaseleccionada, 2);
					stock = (String) this.menu.tblProductos.getValueAt(filaseleccionada, 10);
					this.menu.lblTituloKardex.setText(nombre.toUpperCase());
					this.menu.lblExistenciaKardex.setText(stock);
					Kardex(id);
					this.menu.jdKardex.setSize(1289, 564);
					this.menu.jdKardex.setLocationRelativeTo(null);
					this.menu.jdKardex.setVisible(true);
				}
			} catch (Exception err) {

			}
		}
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		if (e.getSource() == menu.txtBuscarProducto) {
			String Filtrar = menu.txtBuscarProducto.getText();
			MostrarProductos(Filtrar);
		}
		if (e.getSource() == menu.txtBuscarPorNombre) {
			String Buscar = menu.txtBuscarPorNombre.getText();
			MostrarProductosVender(Buscar);
		}
		if (e.getSource() == menu.txtBuscarPorLaboratorio) {
			String laboratorio = menu.txtBuscarPorLaboratorio.getText();
			MostrarPorMarca(laboratorio);
		}
		if (e.getSource() == menu.txtBuscarPorCategoria) {
			String categoria = menu.txtBuscarPorCategoria.getText();
			MostrarPorCategoria(categoria);
		}
		if (e.getSource() == menu.txtBuscarCategoriaAdd) {
			String valor = menu.txtBuscarCategoriaAdd.getText();
			llenarAddCategoria(valor);
		}
		if (e.getSource() == menu.txtLaboratorioAdd) {
			String Nombre = menu.txtLaboratorioAdd.getText();
			llenarAddLaboratorio(Nombre);
		}
		if (e.getSource() == menu.txtCodBarraProducto) {
			String cod = menu.txtCodBarraProducto.getText();
			//el metodod ExistCodBarra no devuelve true si ya existe y false si no existe
			this.productosModel.ExitsCodBarra(cod);
			//validamos si el codBarra no existe va a habilitar el boton guardar si ya existe el deshabilitara el boton guardar
			if (!this.productosModel.isExiste()) {
				menu.lblErrorCodBarra.setText("");
				//si el boton actualizar esta deshabilitado el boton guardar se habilitara de lo contrario se deshabilitara
				if (!menu.btnActualizarProducto.isEnabled()) {
					menu.btnGuardarProducto.setEnabled(true);
				} else {
					menu.btnGuardarProducto.setEnabled(false);
				}
			} else {
				menu.lblErrorCodBarra.setText("Oops. el código ya existe..");
				menu.btnGuardarProducto.setEnabled(false);
			}
		}
	}

	public boolean isNumeric(String cadena) {//metodo para la validacion de campos numericos
		try {
			Float.parseFloat(cadena);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public static void MostrarProductos(String buscar) {
		menu.tblProductos.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
		menu.tblProductos.getTableHeader().setOpaque(false);
		menu.tblProductos.getTableHeader().setBackground(new Color(69, 76, 89));
		menu.tblProductos.getTableHeader().setForeground(new Color(255, 255, 255));
		menu.tblProductos.setModel(productosModel.Consulta(buscar));
		menu.tblProductos.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 35));
	}

	public static void MostrarProductosVender(String Buscar) {
		menu.tblAddProductoFactura.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
		menu.tblAddProductoFactura.getTableHeader().setOpaque(false);
		menu.tblAddProductoFactura.getTableHeader().setBackground(new Color(69, 76, 89));
		menu.tblAddProductoFactura.getTableHeader().setForeground(new Color(255, 255, 255));
		menu.tblAddProductoFactura.setModel(factura.BusquedaGeneralProductoVender(Buscar));
		menu.tblAddProductoFactura.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 35));
	}

	public void LimpiarProducto()//metodo para limpiar los campos de formulario Productos
	{
		menu.txtCodBarraProducto.setText("");
		menu.txtNombreProducto.setText("");
		menu.spPrecioCompra.setValue(0);
		menu.spPrecioVenta.setValue(0);
		menu.txtCantidadProducto.setValue(0);
		menu.spPrecioMinimoProducto.setValue(0);
		menu.spUtilidad.setValue(0);
		menu.txtCategoriaProducto.setText("");
		menu.txtLaboratorioProducto.setText("");
		menu.txtUbicacionProducto.setText("");
		menu.txtDescripcionProducto.setText("");
		menu.txtGananciaProducto.setText("");
		menu.txtMargenGanancia.setText("");
		menu.cmbMonedaCompraProducto.setSelectedItem("Córdobas");
		menu.cmbMonedaVentaProducto.setSelectedItem("Córdobas");
	}

	public void HabilitarProductos() {
		menu.txtCodBarraProducto.setEnabled(true);
		menu.txtNombreProducto.setEnabled(true);
		menu.spPrecioCompra.setEnabled(true);
		menu.spPrecioVenta.setEnabled(true);
		menu.jcFechaVProducto.setEnabled(true);
		menu.txtCantidadProducto.setEnabled(true);
		menu.txtCategoriaProducto.setEnabled(true);
		menu.txtLaboratorioProducto.setEnabled(true);
		menu.txtUbicacionProducto.setEnabled(true);
		menu.txtDescripcionProducto.setEnabled(true);
		menu.txtGananciaProducto.setEnabled(true);
		menu.txtMargenGanancia.setEnabled(true);
		menu.btnCalcularGanancia.setEnabled(true);
		menu.btnGuardarProducto.setEnabled(true);
		menu.btnActualizarProducto.setEnabled(false);
		menu.cmbMonedaCompraProducto.setEnabled(true);
		menu.cmbMonedaVentaProducto.setEnabled(true);
		menu.spPrecioMinimoProducto.setEnabled(true);
		menu.spUtilidad.setEnabled(true);
	}

	public void HabilitarProductosParaEditar() {

		if (this.permiso == 1) {
			menu.txtCodBarraProducto.setEnabled(true);
			menu.txtNombreProducto.setEnabled(true);
			menu.spPrecioCompra.setEnabled(true);
			menu.spPrecioVenta.setEnabled(true);
			menu.jcFechaVProducto.setEnabled(true);
			menu.txtCantidadProducto.setEnabled(true);
			menu.txtCategoriaProducto.setEnabled(true);
			menu.txtLaboratorioProducto.setEnabled(true);
			menu.txtUbicacionProducto.setEnabled(true);
			menu.txtDescripcionProducto.setEnabled(true);
			menu.txtGananciaProducto.setEnabled(true);
			menu.txtMargenGanancia.setEnabled(true);
			menu.btnCalcularGanancia.setEnabled(true);
			menu.btnGuardarProducto.setEnabled(true);
			menu.btnActualizarProducto.setEnabled(false);
			menu.cmbMonedaCompraProducto.setEnabled(true);
			menu.cmbMonedaVentaProducto.setEnabled(true);
			menu.spPrecioMinimoProducto.setEnabled(true);
			menu.spUtilidad.setEnabled(true);
		} else if (this.permiso == 2) {
			menu.txtCodBarraProducto.setEnabled(true);
			menu.txtNombreProducto.setEnabled(true);
			menu.spPrecioCompra.setEnabled(true);
			menu.spPrecioVenta.setEnabled(false);
			menu.jcFechaVProducto.setEnabled(true);
			menu.txtCantidadProducto.setEnabled(false);
			menu.txtCategoriaProducto.setEnabled(true);
			menu.txtLaboratorioProducto.setEnabled(true);
			menu.txtUbicacionProducto.setEnabled(true);
			menu.txtDescripcionProducto.setEnabled(true);
			menu.txtGananciaProducto.setEnabled(true);
			menu.txtMargenGanancia.setEnabled(true);
			menu.btnCalcularGanancia.setEnabled(true);
			menu.btnGuardarProducto.setEnabled(true);
			menu.btnActualizarProducto.setEnabled(false);
			menu.cmbMonedaCompraProducto.setEnabled(true);
			menu.cmbMonedaVentaProducto.setEnabled(true);
			menu.spPrecioMinimoProducto.setEnabled(false);
			menu.spUtilidad.setEnabled(true);
		}
	}
//este metodo iversion es el mismo metodo que esta en reportes lo repite para no crear una nueva instancia de la clase reportes

	public void inversion() {
		float cordobas = this.productosModel.inversionCordobas(),
			dolar = this.productosModel.inversionDolar(),
			precioDolar = Float.parseFloat(menu.txtPrecioDolarVenta.getText()),
			total = cordobas + (dolar * precioDolar);
		menu.lblProyeccion.setText("" + total);
	}

	public void StockMinimoP(String categoria, float cantidad)//llenar tabla de productosModel bajos de estock
	{
		//los parametros que recibe son para los filtros de bussqueda den la base de datos
		menu.tblStockMin.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
		menu.tblStockMin.getTableHeader().setOpaque(false);
		menu.tblStockMin.getTableHeader().setBackground(new Color(100, 100, 100));
		menu.tblStockMin.getTableHeader().setForeground(new Color(255, 255, 255));
		menu.tblStockMin.setModel(productosModel.MinimoStock(categoria, cantidad));
	}

	//metodo para mostrar los productosModel a vender por filtro de Marca
	public void MostrarPorMarca(String laboratorio) {
		menu.tblAddProductoFactura.setModel(factura.BuscarPorMarca(laboratorio));
	}

	//metodo para mostrar los productosModel a vender por filtro de Categoria
	public void MostrarPorCategoria(String categoria) {
		menu.tblAddProductoFactura.setModel(factura.BuscarPorCategoria(categoria));
	}

	public void llenarAddCategoria(String nombre)//metodo para llenar la tabla de add categoria a productosModel
	{
		menu.tblAddCategoria.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
		menu.tblAddCategoria.getTableHeader().setOpaque(false);
		menu.tblAddCategoria.getTableHeader().setBackground(new Color(100, 100, 100));
		menu.tblAddCategoria.getTableHeader().setForeground(new Color(255, 255, 255));
		menu.tblAddCategoria.setModel(productosModel.MostrarCategorias(nombre));//
	}

	public void llenarAddLaboratorio(String nombre)//metodo para llenar la tabla de add Laboratorio a productosModel
	{
		menu.tblAddLaboratorio.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
		menu.tblAddLaboratorio.getTableHeader().setOpaque(false);
		menu.tblAddLaboratorio.getTableHeader().setBackground(new Color(100, 100, 100));
		menu.tblAddLaboratorio.getTableHeader().setForeground(new Color(255, 255, 255));
		menu.tblAddLaboratorio.setModel(productosModel.MostrarMarca(nombre));
	}

	public void editarProducto() {
		int filaseleccionada;
		String id, codBarra, nombre, categoria, laboratorio, ubicacion, descripcion, monedaCompra, monedaVenta, precioMin;
		Date fechaVencimiento;
		float precioC, precioV, cantidad, precioMinimo, utilidad;
		SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
		try {
			filaseleccionada = menu.tblProductos.getSelectedRow();
			if (filaseleccionada == -1) {
				JOptionPane.showConfirmDialog(null, "Seleccione una fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
			} else {
				modelo = (DefaultTableModel) menu.tblProductos.getModel();
				id = (String) modelo.getValueAt(filaseleccionada, 0);
				codBarra = (String) modelo.getValueAt(filaseleccionada, 1);
				nombre = (String) modelo.getValueAt(filaseleccionada, 2);
				precioC = Float.parseFloat(modelo.getValueAt(filaseleccionada, 3).toString());
				monedaCompra = (String) modelo.getValueAt(filaseleccionada, 4);
				precioV = Float.parseFloat(modelo.getValueAt(filaseleccionada, 5).toString());
				monedaVenta = (String) modelo.getValueAt(filaseleccionada, 6);
				precioMinimo = Float.parseFloat(modelo.getValueAt(filaseleccionada, 7).toString());
				fechaVencimiento = formatoFecha.parse(modelo.getValueAt(filaseleccionada, 9).toString());
				descripcion = (String) modelo.getValueAt(filaseleccionada, 8);
				cantidad = Float.parseFloat(modelo.getValueAt(filaseleccionada, 10).toString());
				categoria = (String) modelo.getValueAt(filaseleccionada, 11);
				laboratorio = (String) modelo.getValueAt(filaseleccionada, 12);
				ubicacion = (String) modelo.getValueAt(filaseleccionada, 13);
				utilidad = Float.parseFloat(modelo.getValueAt(filaseleccionada, 14).toString());
				HabilitarProductosParaEditar();
				LimpiarProducto();
				menu.txtCodBarraProducto.setText(codBarra);
				menu.txtNombreProducto.setText(nombre);
				menu.spPrecioCompra.setValue(precioC);
				menu.cmbMonedaCompraProducto.setSelectedItem(monedaCompra);
				menu.spPrecioVenta.setValue(precioV);
				menu.cmbMonedaVentaProducto.setSelectedItem(monedaVenta);
				menu.jcFechaVProducto.setDate(fechaVencimiento);
				menu.txtCantidadProducto.setValue(cantidad);
				menu.txtCategoriaProducto.setText(productosModel.ObtenerIdCategoria(categoria));
				menu.txtLaboratorioProducto.setText(productosModel.ObtenerIdMarca(laboratorio));
				menu.txtUbicacionProducto.setText(ubicacion);
				menu.txtDescripcionProducto.setText(descripcion);
				menu.spPrecioMinimoProducto.setValue(precioMinimo);
				menu.txtGananciaProducto.setText("0");
				menu.txtMargenGanancia.setText("");
				menu.spUtilidad.setValue(utilidad);
				this.id = id;
				menu.btnGuardarProducto.setEnabled(false);
				menu.btnActualizarProducto.setEnabled(true);
			}
		} catch (Exception err) {
			JOptionPane.showConfirmDialog(null, err + "en la funcion editar producto en CtrlProducto");
		}
	}

	public void guardarProducto() {
		String codigoBarra = menu.txtCodBarraProducto.getText(),
			nombre = menu.txtNombreProducto.getText(),
			precioCProducto = String.valueOf(menu.spPrecioCompra.getValue()),
			precioVProducto = String.valueOf(menu.spPrecioVenta.getValue()),
			ganancia = menu.txtGananciaProducto.getText(),
			cantidad = String.valueOf(menu.txtCantidadProducto.getValue()),
			categoria = menu.txtCategoriaProducto.getText(),
			laboratorio = menu.txtLaboratorioProducto.getText(),
			ubicacion = menu.txtUbicacionProducto.getText(),
			descripcion = menu.txtDescripcionProducto.getText(),
			monedaCompra = menu.cmbMonedaCompraProducto.getSelectedItem().toString(),
			monedaVenta = menu.cmbMonedaVentaProducto.getSelectedItem().toString();
		float utilidad = Float.parseFloat(menu.spUtilidad.getValue().toString()),
			precioMinimo = Float.parseFloat(menu.spPrecioMinimoProducto.getValue().toString());
		Date fechaVencimiento = menu.jcFechaVProducto.getDate();
		long fechaV = fechaVencimiento.getTime();
		java.sql.Date fecha = new java.sql.Date(fechaV);
		//Validacion de que sean ingresados los datos correctos y que no esten vacios
		if (nombre.equals("")) {
			//JOptionPane.showMessageDialog(null, "Llene el campo Nombre", "Advertencia", JOptionPane.WARNING_MESSAGE);
		} else if (fechaVencimiento.equals(null)) {
			JOptionPane.showMessageDialog(null, "Llene el campo Fecha Vencimiento", "Advertencia", JOptionPane.WARNING_MESSAGE);
		} else {
			//funcion Guardar de la clase Productos para guardar productosModel
			if (!isNumeric(categoria)) {
				categoria = "1";
			}
			if (!isNumeric(laboratorio)) {
				laboratorio = "1";
			}
			productosModel.Guardar(codigoBarra, nombre, precioCProducto, monedaCompra, precioVProducto, monedaVenta, precioMinimo, fecha, cantidad, categoria, laboratorio, ubicacion, descripcion, utilidad);
			guardarKardex(cantidad);
			MostrarProductos("");
			LimpiarProducto();
			menu.btnGuardarProducto.setEnabled(true);
			menu.btnActualizarProducto.setEnabled(false);
			menu.txtCodBarraProducto.requestFocus();
			MostrarProductosVender("");
			inversion();

		}
	}

	//guardar en el kardex el stock inicial
	public void guardarKardex(String cantidad) {
		int producto = this.productosModel.ultimoRegistro();
		String user = menu.lblUsuarioSistema.getText(), anotacion = "inicial";
		this.productosModel.guardarKardexIncial(producto, user, Float.parseFloat(cantidad), anotacion);

	}

	public void actualizarProducto() {
		String codigoBarra = menu.txtCodBarraProducto.getText(),
			nombre = menu.txtNombreProducto.getText(),
			precioCProducto = String.valueOf(menu.spPrecioCompra.getValue()),
			precioVProducto = String.valueOf(menu.spPrecioVenta.getValue()),
			ganancia = menu.txtGananciaProducto.getText(),
			cantidad = String.valueOf(menu.txtCantidadProducto.getValue()),
			categoria = menu.txtCategoriaProducto.getText(),
			laboratorio = menu.txtLaboratorioProducto.getText(),
			ubicacion = menu.txtUbicacionProducto.getText(),
			descripcion = menu.txtDescripcionProducto.getText(),
			monedaCompra = menu.cmbMonedaCompraProducto.getSelectedItem().toString(),
			monedaVenta = menu.cmbMonedaVentaProducto.getSelectedItem().toString(),
			precioMinimo = menu.spPrecioMinimoProducto.getValue().toString(),
			user = this.menu.lblUsuarioSistema.getText();
		float utilidad = Float.parseFloat(menu.spUtilidad.getValue().toString());
		Date fechaVencimiento = menu.jcFechaVProducto.getDate();
		long fechaV = fechaVencimiento.getTime();
		java.sql.Date fecha = new java.sql.Date(fechaV);
		if (nombre.equals("")) {
			//JOptionPane.showMessageDialog(null, "Llene el campo Nombre", "Advertencia", JOptionPane.WARNING_MESSAGE);
		} /*else if (precioCProducto.equals("")) {
                JOptionPane.showMessageDialog(null, "Llene el campo Precio Compra", "Advertencia", JOptionPane.WARNING_MESSAGE);
            } */ else if (precioVProducto.equals("")) {
			JOptionPane.showMessageDialog(null, "Llene el campo Precio Venta", "Advertencia", JOptionPane.WARNING_MESSAGE);
		} else if (fechaVencimiento.equals("")) {
			JOptionPane.showMessageDialog(null, "Llene el campo Fecha Vencimiento", "Advertencia", JOptionPane.WARNING_MESSAGE);
		} else if (cantidad.equals("")) {
			JOptionPane.showMessageDialog(null, "Llene el campo Cantidad", "Advertencia", JOptionPane.WARNING_MESSAGE);
		}/* else if (categoria.equals("")) {
                JOptionPane.showMessageDialog(null, "Llene el campo Categoria", "Advertencia", JOptionPane.WARNING_MESSAGE);
            } else if (laboratorio.equals("")) {
                JOptionPane.showMessageDialog(null, "Llene el campo Laboratorio", "Advertencia", JOptionPane.WARNING_MESSAGE);
            } */ else {//validacion para ingreso de nuemeros 
			/*if (!isNumeric(precioCProducto)) {
                    JOptionPane.showMessageDialog(null, "Solo numeros campo Precio Compra", "Advertencia", JOptionPane.WARNING_MESSAGE);
                } else*/ if (!isNumeric(precioVProducto)) {
				JOptionPane.showMessageDialog(null, "Solo numeros campo Precio Venta", "Advertencia", JOptionPane.WARNING_MESSAGE);
			} else if (!isNumeric(cantidad)) {
				JOptionPane.showMessageDialog(null, "Solo numeros campo Cantidad", "Advertencia", JOptionPane.WARNING_MESSAGE);
			} else {   //funcion Guardar de la clase Productos para guardar productosModel
				if (!isNumeric(categoria)) {
					categoria = "1";
				}
				if (!isNumeric(laboratorio)) {
					laboratorio = "1";
				}
				productosModel.Actualizar(this.id, codigoBarra, nombre, precioCProducto, monedaCompra, precioVProducto, monedaVenta, precioMinimo, fecha, cantidad, categoria, laboratorio, ubicacion, descripcion, utilidad);
				this.productosModel.guardarKardexIncial(Integer.parseInt(this.id), user, Float.parseFloat(cantidad), "edicion stock");
				MostrarProductos("");
				MostrarProductosVender("");
				LimpiarProducto();
				inversion();
				menu.btnGuardarProducto.setEnabled(true);
				menu.btnActualizarProducto.setEnabled(false);
			}

		}
	}

	public void calcularGanancia() {
		float compra, porcentaje, resultado;
		if (isNumeric(menu.txtGananciaProducto.getText())) {
			compra = (float) menu.spPrecioCompra.getValue();
			porcentaje = compra * Float.parseFloat(menu.txtGananciaProducto.getText()) / 100;
			resultado = porcentaje + compra;
			menu.txtMargenGanancia.setText(String.valueOf(porcentaje));
			menu.spPrecioVenta.setValue(resultado);
		} else {
			JOptionPane.showMessageDialog(null, "Solo Numero en Campo Precio Compra y % Ganacia");
		}
	}

	public void borrarProducto() {
		int filaseleccionada = menu.tblProductos.getSelectedRow();
		String id;
		try {
			filaseleccionada = menu.tblProductos.getSelectedRow();
			if (filaseleccionada == -1) {
				JOptionPane.showMessageDialog(null, "Seleccione una Fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
			} else {
				int confirmar = JOptionPane.showConfirmDialog(null, "Seguro Que Quieres Borrar Este Producto", "Advertencia", JOptionPane.OK_CANCEL_OPTION);
				if (confirmar == JOptionPane.YES_OPTION) {
					modelo = (DefaultTableModel) menu.tblProductos.getModel();
					id = (String) modelo.getValueAt(filaseleccionada, 0);
					productosModel.Eliminar(id);
					MostrarProductos("");
					inversion();
				}

			}
		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, err + "borrar producto");
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		if (e.getSource() == menu.txtCategoriaProducto) {
			menu.AddCategoria.setSize(600, 200);
			menu.AddCategoria.setVisible(true);
			menu.AddCategoria.setLocationRelativeTo(null);
			llenarAddCategoria("");
		}
		if (e.getSource() == menu.txtLaboratorioProducto) {
			menu.AddMarca.setSize(562, 200);
			menu.AddMarca.setVisible(true);
			menu.AddMarca.setLocationRelativeTo(null);
			llenarAddLaboratorio("");
		}
		if (e.getSource() == menu.tblAddCategoria) {
			String id;
			int filaseleccionada = menu.tblAddCategoria.getSelectedRow();
			try {
				if (filaseleccionada == -1) {
					JOptionPane.showMessageDialog(null, "Seleccione una Fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
				} else {
					modelo = (DefaultTableModel) menu.tblAddCategoria.getModel();
					id = (String) modelo.getValueAt(filaseleccionada, 0);
					menu.txtCategoriaProducto.setText(id);
					menu.AddCategoria.setVisible(false);
				}
			} catch (Exception err) {

			}
		}
		if (e.getSource() == menu.tblAddLaboratorio) {
			String id;
			int filaseleccionada = menu.tblAddLaboratorio.getSelectedRow();
			try {
				if (filaseleccionada == -1) {
					JOptionPane.showMessageDialog(null, "Seleccione una Fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
				} else {
					modelo = (DefaultTableModel) menu.tblAddLaboratorio.getModel();
					id = (String) modelo.getValueAt(filaseleccionada, 0);
					menu.txtLaboratorioProducto.setText(id);
					menu.AddMarca.setVisible(false);
				}
			} catch (Exception err) {

			}
		}
		if (e.getSource() == menu.btnGenerarReporteStock) {
			float cant = 0;
			if (menu.txtCantidadStockM.getText().equals("")) {

			} else {
				cant = Float.parseFloat(menu.txtCantidadStockM.getText());
				try {
					productosModel.GenerarReporteStockMin(menu.txtCategoriaStockM.getText(), cant);
				} catch (SQLException ex) {
					Logger.getLogger(CtrlProducto.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public void DeshabilitarProductos() {
		menu.txtCodBarraProducto.setEnabled(false);
		menu.txtNombreProducto.setEnabled(false);
		menu.spPrecioCompra.setEnabled(false);
		menu.spPrecioVenta.setEnabled(false);
		menu.jcFechaVProducto.setEnabled(false);
		menu.txtCantidadProducto.setEnabled(false);
		menu.txtCategoriaProducto.setEnabled(false);
		menu.txtLaboratorioProducto.setEnabled(false);
		menu.txtUbicacionProducto.setEnabled(false);
		menu.txtDescripcionProducto.setEnabled(false);
		menu.txtGananciaProducto.setEnabled(false);
		menu.txtMargenGanancia.setEnabled(false);
		menu.btnCalcularGanancia.setEnabled(false);
		menu.btnGuardarProducto.setEnabled(false);
		menu.btnActualizarProducto.setEnabled(false);
		menu.cmbMonedaCompraProducto.setEnabled(false);
		menu.cmbMonedaVentaProducto.setEnabled(false);
		menu.spPrecioMinimoProducto.setEnabled(false);
		menu.spUtilidad.setEnabled(false);
	}

	public void ImprimirMinStock() {
		int filas = menu.tblStockMin.getRowCount();
		String template = "Productos bajos en existencia. \n--------------------------------------\n";
		if (filas != 0) {
			for (int i = 0; i < filas; i++) {
				template += menu.tblStockMin.getValueAt(i, 1) + " " + menu.tblStockMin.getValueAt(i, 2) + "[] \n";
			}
			try {
				escpos.write(imageWrapper, escposImage).feed(2);
				escpos.writeLF(template).feed(2).cut(EscPos.CutMode.FULL);
				escpos.close();
			} catch (Exception e) {

			}
		} else {

		}
	}

	public void Kardex(String id) {
		this.menu.lblSalidasKardex.setText(this.productosModel.countKardexSalidas(id));
		this.menu.tblKardexS.setModel(this.productosModel.kardexSalidas(id));
		this.menu.tblKardexE.setModel(this.productosModel.kardexEntradas(id));
		this.menu.lblStockInicialKardex.setText(this.productosModel.kardexInicial(id));
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.VK_ENTER == e.getKeyCode()) {
			//guardara solo si el boton guardar esta habilitado
			if (menu.btnGuardarProducto.isEnabled()) {
				guardarProducto();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
