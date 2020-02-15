package NumeroOculto_Cliente_Servidor;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Font;


public class Cliente extends JFrame implements ActionListener, Runnable
{
	private static final long serialVersionUID = 1L;
	private JTextField mensaje = new JTextField();
	private JScrollPane scrollpane;
	private JTextArea textarea;
	private JButton boton_enviar = new JButton("Enviar");
	private JButton desconectar = new JButton("Salir");
	private ServerSocket serverSocket;
	private static String nombre;
	private boolean llave = false;
	private static boolean llave_bucle = true;

	private static String ip;
	private final JLabel lblIntroduceUnNmero = new JLabel("INTRODUCE UN N\u00DAMERO ENTRE 0 Y 100");

	public Cliente()
	{
		super("Usuario: " + nombre);
		getContentPane().setBackground(Color.DARK_GRAY);
		getContentPane().setLayout(null);
		setSize(550, 400);
		setLocationRelativeTo(null);
		mensaje.setFont(new Font("Arial", Font.BOLD, 13));
		mensaje.setBounds(10, 48, 400, 29);
		getContentPane().add(mensaje);
		textarea = new JTextArea();
		textarea.setFont(new Font("Arial", Font.ITALIC, 13));
		scrollpane = new JScrollPane(textarea);
		scrollpane.setBounds(10, 88, 400, 262);
		getContentPane().add(scrollpane);
		boton_enviar.setBackground(Color.YELLOW);
		boton_enviar.setBounds(420, 48, 100, 30);
		getContentPane().add(boton_enviar);
		desconectar.setBackground(Color.YELLOW);
		desconectar.setBounds(420, 11, 100, 30);
		getContentPane().add(desconectar);
		textarea.setEditable(false);
		boton_enviar.addActionListener(this);
		getRootPane().setDefaultButton(boton_enviar);
		lblIntroduceUnNmero.setHorizontalAlignment(SwingConstants.CENTER);
		lblIntroduceUnNmero.setForeground(Color.GREEN);
		lblIntroduceUnNmero.setFont(new Font("Arial", Font.BOLD, 13));
		lblIntroduceUnNmero.setBounds(10, 18, 400, 14);

		getContentPane().add(lblIntroduceUnNmero);
		desconectar.addActionListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);

		Thread hilo = new Thread(this);
		hilo.start();

		//NOTIFICA QUE TE HAS CONECTADO CON TU IP
		try
		{                              

			//OBTENEMOS LA IP LOCAL DE LA MÁQUINA
			InetAddress address = InetAddress.getLocalHost();
			String ip_local = address.getHostAddress();

			Socket socket_envio = new Socket(ip, 7247);
			DataOutputStream flujo_salida = new DataOutputStream(socket_envio.getOutputStream());
			flujo_salida.writeUTF(ip_local);
			flujo_salida.close();
			socket_envio.close();


		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			textarea.append("\n \n ***-----FIN DEL JUEGO-----***");
			mensaje.setEditable(false);
			boton_enviar.setEnabled(false);
		}



	}

	public static void main(String[] args)
	{
		ip = JOptionPane.showInputDialog("Introduce la IP a la que te quieres conectar:");
		do 
		{
			nombre = JOptionPane.showInputDialog("Introduce tu nombre o nick:");

		} while (nombre.length() > 7);


		new Cliente().setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource().equals(boton_enviar))
		{              
			try
			{                              
				Socket socket_envio = new Socket(ip, 7247);
				String texto = mensaje.getText().toString();
				DataOutputStream flujo_salida = new DataOutputStream(socket_envio.getOutputStream());
				flujo_salida.writeUTF(nombre + "--" +texto);
				flujo_salida.close();
				socket_envio.close();
				mensaje.setText("");
				mensaje.requestFocus();
				
				try 
				{
					mensaje.setEditable(false);
					Thread.sleep(3000);
					mensaje.setEditable(true);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (Exception e)
			{
				System.out.println(e.getMessage());
				textarea.append("\n \n ***-----FIN DEL JUEGO-----***");
				scrollpane.getVerticalScrollBar().setValue(scrollpane.getVerticalScrollBar().getMaximum());
				mensaje.setEditable(false);
				boton_enviar.setEnabled(false);
			}                                                             
		}
		if(ae.getSource().equals(desconectar))
		{
			try
			{                              

				if (boton_enviar.isEnabled())     //SI EL BOTON ENVIAR ESTÁ HABILITADO SIGNIFICA QUE NADIE HA GANADO POR LO QUE ENVIAMOS NUESTRA IP PARA QUE SE ELIMINE DE LA LISTA DEL SERVIDOR, YA QUE ESTE AÚN SIGUE ESCUCHANDO AL NO HABER FINALIZADO                
				{
					//OBTENEMOS LA IP LOCAL DE LA MÁQUINA Y LA ENVIAMOS AL SERVIDOR PARA QUE LA REGISTRE
					InetAddress address = InetAddress.getLocalHost();
					String ip_local = address.getHostAddress();

					Socket socket_envio = new Socket(ip, 7247);
					DataOutputStream flujo_salida = new DataOutputStream(socket_envio.getOutputStream());
					flujo_salida.writeUTF(ip_local);
					flujo_salida.close();
					socket_envio.close();
				}

				
				llave_bucle = false;
				System.exit(0);


			} catch (Exception e)
			{
				llave_bucle = false;
				System.out.println(e.getMessage());
				textarea.append("\n \n ***-----FIN DEL JUEGO-----***");
				scrollpane.getVerticalScrollBar().setValue(scrollpane.getVerticalScrollBar().getMaximum());
				mensaje.setEditable(false);
				boton_enviar.setEnabled(false);
			}                                                                                             
		}

		


	}

	@Override
	public void run() 
	{
		try
		{              
			int puerto = 8888;
			serverSocket = new ServerSocket(puerto);

			while(llave_bucle)
			{
				Socket socket_recibo = serverSocket.accept();
				DataInputStream flujo_entrada = new DataInputStream(socket_recibo.getInputStream());
				String mensaje_entrada = flujo_entrada.readUTF();

				for (int i = 0; i < mensaje_entrada.length(); i++)
				{
					char caracterString = '-';

					if (caracterString == mensaje_entrada.charAt(i))
					{
						llave = true;
						break;
					}
				}


				if (llave)
				{
					boton_enviar.setEnabled(false);
					mensaje.setEnabled(false);
				}
				else 
				{
					textarea.setText(mensaje_entrada + "\n");
					scrollpane.getVerticalScrollBar().setValue(scrollpane.getVerticalScrollBar().getMaximum());
					flujo_entrada.close();
					socket_recibo.close();
				}


			}

		} catch (IOException  e)
		{                              
			try
			{
				llave_bucle = false;
				serverSocket.close();

			} catch (IOException e1)
			{
				System.out.println(e1.getMessage());
			}                              
		}                              
	}
}