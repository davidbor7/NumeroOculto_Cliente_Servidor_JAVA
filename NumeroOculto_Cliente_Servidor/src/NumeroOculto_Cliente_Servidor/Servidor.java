package NumeroOculto_Cliente_Servidor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;


public class Servidor extends JFrame implements ActionListener, Runnable
{
	private static final long serialVersionUID = 1L;
	private JScrollPane scrollpane;
	static JTextArea textarea;
	private JButton boton = new JButton("Apagar Servidor");
	private static JLabel lblPuerto;
	private ServerSocket serverSocket;
	private int numero_aleatorio;
	private List<String> conexiones;
	private boolean llave_fin;
	private static boolean llave_bucle_hilo;
	private static String ip_servidor;
	private static int puerto = 7247;
	private JScrollPane scrollpane2;
	private JTextArea textArea2;
	private JLabel label;

	public Servidor() 
	{
		super("SALA DE CHAT");
		getContentPane().setForeground(Color.GREEN);
		setBackground(Color.BLACK);
		getContentPane().setBackground(Color.DARK_GRAY);
		setTitle("N\u00FAmero Oculto");
		getContentPane().setLayout(null);
		setSize(571, 415);
		setLocationRelativeTo(null);
		textarea = new JTextArea();
		textarea.setFont(new Font("Arial", Font.BOLD, 13));
		textarea.setForeground(Color.GREEN);
		textarea.setBackground(Color.BLACK);
		scrollpane = new JScrollPane(textarea);
		scrollpane.setBounds(10, 10, 382, 340);
		getContentPane().add(scrollpane);
		boton.setIcon(null);
		boton.setForeground(Color.BLACK);
		boton.setBackground(Color.RED);
		boton.setBounds(402, 9, 153, 30);
		getContentPane().add(boton);
		textarea.setEditable(false);
		boton.addActionListener(this);
		this.getRootPane().setDefaultButton(boton);
		lblPuerto = new JLabel("PUERTO: " + puerto);
		lblPuerto.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 13));
		lblPuerto.setForeground(new Color(0, 255, 0));
		lblPuerto.setHorizontalAlignment(SwingConstants.CENTER);
		lblPuerto.setBounds(412, 82, 143, 14);
		getContentPane().add(lblPuerto);


		JLabel lblIpServidor = new JLabel("IP: " + ip_servidor);
		lblIpServidor.setHorizontalAlignment(SwingConstants.CENTER);
		lblIpServidor.setForeground(new Color(0, 255, 0));
		lblIpServidor.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 13));
		lblIpServidor.setBounds(412, 97, 143, 14);
		getContentPane().add(lblIpServidor);

		scrollpane2 = new JScrollPane((Component) null);
		scrollpane2.setBounds(417, 185, 117, 165);
		getContentPane().add(scrollpane2);

		textArea2 = new JTextArea();
		textArea2.setForeground(Color.GREEN);
		textArea2.setFont(new Font("Arial", Font.BOLD, 13));
		textArea2.setEditable(false);
		textArea2.setBackground(Color.BLACK);
		scrollpane2.setViewportView(textArea2);

		JLabel lblUsuariosConectados = new JLabel("USUARIOS ONLINE");
		lblUsuariosConectados.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsuariosConectados.setForeground(Color.GREEN);
		lblUsuariosConectados.setFont(new Font("Arial", Font.BOLD, 13));
		lblUsuariosConectados.setBounds(402, 160, 153, 14);
		getContentPane().add(lblUsuariosConectados);

		label = new JLabel("");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setForeground(Color.GREEN);
		label.setFont(new Font("Arial", Font.BOLD, 13));
		label.setBounds(10, 361, 382, 14);
		getContentPane().add(label);
		
		JLabel lblNmero = new JLabel("N\u00DAMERO:");
		lblNmero.setHorizontalAlignment(SwingConstants.CENTER);
		lblNmero.setForeground(Color.GREEN);
		lblNmero.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 13));
		lblNmero.setBounds(412, 115, 143, 14);
		getContentPane().add(lblNmero);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
		setResizable(false);


		llave_bucle_hilo = true;
		llave_fin = false;
		conexiones = new ArrayList<String>();
		numero_aleatorio = (int) (Math.random() * 100) + 1;
		lblNmero.setText("NÚMERO: " + numero_aleatorio);
		Thread hilo = new Thread(this);
		hilo.start();


	}

	public static void main(String[] args)
	{
		ip_servidor = JOptionPane.showInputDialog("Introduce la IP de tu servidor:");
		new Servidor();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{              
		try
		{
			llave_bucle_hilo=false;
			serverSocket.close();


		} catch (IOException e1)
		{
			e1.printStackTrace();
			System.out.println("Hola");
		}
		System.exit(0);
	}

	@Override
	public void run()
	{
		try
		{              
			int puerto = 7247;
			serverSocket = new ServerSocket(puerto);



			while(llave_bucle_hilo)
			{
				Socket socket_recibo = serverSocket.accept();

				synchronized (this) //LO HACEMOS SYNCRONIZED PARA QUE NO GANEN DOS JUGADORES A LA VEZ
				{

					DataInputStream flujo_entrada = new DataInputStream(socket_recibo.getInputStream());
					String mensaje_entrada = flujo_entrada.readUTF();       


					if (!comprueba_si_es_ip(mensaje_entrada)==true) 
					{

						String [] mensaje_desagregado= mensaje_entrada.split("--");                    

						try
						{
							int numero = Integer.valueOf(mensaje_desagregado[1]); //OBTENEMOS EL NÚMERO DE LA CADENA RECIBIDA(SEPARAMOS DEL NOMBRE DE USUARIO)

							if (numero == numero_aleatorio)
							{


								socket_recibo.close();
								serverSocket.close();
								textarea.append(mensaje_desagregado[0]+ ">>> " + numero +"    ***¡¡¡HA ACERTADO!!!***" + "\n");
								scrollpane.getVerticalScrollBar().setValue(scrollpane.getVerticalScrollBar().getMaximum());
								label.setText("--EL SERVIDOR HA FINALIZADO CORRECTAMENTE--");
								System.out.println("--EL SERVIDOR HA FINALIZADO CORRECTAMENTE--");
								textArea2.setText(""); //BORRAMOS TODAS LAS IP CONECTADAS
								llave_fin = true;
								llave_bucle_hilo = false;

							}
							else 
							{
								if (numero < numero_aleatorio)
								{
									textarea.append(mensaje_desagregado[0]+ "> " + numero +"    El número a acertar es mayor." + "\n");
									scrollpane.getVerticalScrollBar().setValue(scrollpane.getVerticalScrollBar().getMaximum());
								}
								else 
								{
									textarea.append(mensaje_desagregado[0]+ "> " + numero +"    El número a acertar es menor." + "\n");
									scrollpane.getVerticalScrollBar().setValue(scrollpane.getVerticalScrollBar().getMaximum());
								}
							}                                                                             


						} catch (Exception e)
						{
							System.out.println("No has introducido un número válido.");
							flujo_entrada.close(); //CERRAMOS LOS FLUJOS DE ENTRADA
							socket_recibo.close(); //CERRAMOS LOS FLUJOS DE ENTRADA
						}

						flujo_entrada.close(); //CERRAMOS LOS FLUJOS DE ENTRADA
						socket_recibo.close(); //CERRAMOS LOS FLUJOS DE ENTRADA



						//-------------------------ENVIAMOS TODO LO QUE HAY EN NUESTRO TEXTAREA PARA DUPLICAL LA INFORMACIÓN DEL SERVIDOR EN LOS CLIENTES-----------------------------


						for (int i = 0; i < conexiones.size(); i++)
						{

							Socket socket_envio = new Socket(conexiones.get(i), 8888);
							String mensaje_salida = textarea.getText().toString();
							DataOutputStream flujo_salida = new DataOutputStream(socket_envio.getOutputStream());
							flujo_salida.writeUTF(mensaje_salida);
							flujo_salida.close();
							socket_envio.close();
						}


						//------------------------------------------------------------------------------------------------------------------------------------------------------------                                                             

						if (llave_fin == true)
						{
							for (int i = 0; i < conexiones.size(); i++)
							{

								Socket socket_envio = new Socket(conexiones.get(i), 8888);
								String mensaje_salida = textarea.getText().toString() + "\n\n-------------------------------------FIN DEL JUEGO-------------------------------------";
								DataOutputStream flujo_salida = new DataOutputStream(socket_envio.getOutputStream());
								flujo_salida.writeUTF(mensaje_salida);
								flujo_salida.close();
								socket_envio.close();
							}

						}

					}
					else //EN CASO DE MANDAR EL CLIENTE LA IP (SE COMPRUEBA MEDIANTE EL METODO COMPRUEBA_SI_ES_IP) SE ELIMINARÁ DICHA IP DEL ARRAYLIST, PARA QUE NO HAY PROBLEMAS CUANDO SE DESCONECTE Y CONECTEN CLIENTES NUEVOS
					{

						if (conexiones.contains(mensaje_entrada)==true)
						{

							System.out.println("-"+mensaje_entrada + " se ha desconectado-");                                                           

							//ELIMINA LA IP DEL ARRAYLIST
							for (int i = 0; i < conexiones.size(); i++) 
							{


								if ((conexiones.get(i).equals(mensaje_entrada)))
								{
									conexiones.remove(i);
								}

							}


							textArea2.setText(""); //LIMPIAMOS TODAS LAS CONEXIONES
							actualiza_usuarios(); //RECARGAMOS EL ARRAY LIST CON TODAS LAS CONEXIONES

						}
						else
						{
							System.out.println("-"+mensaje_entrada + " se ha conectado-");
							conexiones.add(mensaje_entrada);//ACUMULA LAS IP DE LAS CONEXIONES QUE SE VAN AGREGANDO A NUESTRO SERVIDOR

							textArea2.setText(""); //LIMPIAMOS TODAS LAS CONEXIONES
							actualiza_usuarios();
						}                                              
					}


					flujo_entrada.close(); //CERRAMOS LOS FLUJOS DE ENTRADA
					socket_recibo.close(); //CERRAMOS LOS FLUJOS DE ENTRADA
				}
			}
		}
		catch (IOException e)
		{              
			try
			{
				llave_bucle_hilo=false;
				serverSocket.close();
				System.out.println("FIN DEL PROGRAMA.");

			} catch (IOException e1)
			{
				System.out.println("FIN DEL PROGRAMA.");
			}
		}              
	}
	public boolean comprueba_si_es_ip (String cadena)
	{
		String candena_ = cadena;
		boolean llave = false;
		int contador = 0;

		for (int i = 0; i < candena_.length(); i++) 
		{
			char c = candena_.charAt(i);
			if (c == '.') 
			{
				contador ++;
			}
		}

		if (contador == 3) 
		{
			llave = true;
		}

		return llave;

	}

	public void actualiza_usuarios() 
	{

		for (int i = 0; i < conexiones.size(); i++) 
		{                              
			textArea2.append("··· "+conexiones.get(i).toString()+" ···\n");
		}              
	}
}