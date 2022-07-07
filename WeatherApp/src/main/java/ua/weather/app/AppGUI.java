/* Course work
 * Author: Yaremenko Andrii 
 * Group: knd-12
 * Date: 2022-24-05
 */

package ua.weather.app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class AppGUI extends JFrame{

	private static final long serialVersionUID = 1L;
	// функціональні кнопки/поля
	private JTextField input = new JTextField("");
	private JLabel textday = new JLabel("<html>Кількість днів</html>", SwingConstants.CENTER);
	private JButton buttonSearch = new JButton("Дізнатись погоду"); 
	private JButton buttonCopy = new JButton("Скопіювати до буферу обміну");
	private JButton buttonBrowser = new JButton("Відкрити сайт");
	private JButton buttonFile = new JButton("Створити файл з прогнозом");
	private JSlider slider = new JSlider();
	private String text = "";
	private DataProcessing dp = new DataProcessing();
	
	public AppGUI() {
		super("Погода"); //Назва вікна
		setBounds(0,0,340,250); //Розміри вікна
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //Кнопка "закрити"
		setVisible(true);
		setLocationRelativeTo(null);//Поява вікна по центру екрану
		setResizable(false); //Фіксація розмірів вікна
		ImageIcon img = new ImageIcon("src/icon.png");
		setIconImage(img.getImage());
		
		setLayout(new GridBagLayout());
		//Розташування елементів
		add(input,new GridBagConstraints(0,0,0,0,1,1,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(5,15,15,15),0,0));
		add(textday,new GridBagConstraints(0,0,0,0,1,1,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(30,5,5,5),0,0));
		add(slider,new GridBagConstraints(0,0,0,0,1,1,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(50,15,15,15),0,0));
		add(buttonSearch,new GridBagConstraints(0,0,0,0,1,1,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(80,15,15,15),0,0));
		add(buttonCopy,new GridBagConstraints(0,0,0,0,1,1,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(110,15,15,15),0,0));
		add(buttonBrowser,new GridBagConstraints(0,0,0,0,1,1,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(140,15,15,15),0,0));
		add(buttonFile,new GridBagConstraints(0,0,0,0,1,1,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(170,15,15,15),0,0));
		
		slider.setMinimum(1); // Мінімальне значення
		slider.setMaximum(7); // Максимальне значення
		slider.setValue(1); // Початкове значення
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(1);	

		buttonSearch.addActionListener(new ButtonSearchEventListener()); //Івенти при натисканні на кнопки
		buttonCopy.addActionListener(new ButtonCopyEventListener());
		buttonBrowser.addActionListener(new ButtonBrowserEventListener());
		buttonFile.addActionListener(new ButtonFileEventListener());
	}
	
	class DataProcessing {
		public void request() {
			String url = "https://ua.sinoptik.ua/погода-"+input.getText();
			Document doc = null;
			
			//Обробка винятків - відсутність інтернету чи невірний населений пункт
			try { 
				doc = Jsoup.connect(url).get();
			} catch (NullPointerException exc1) {
				JOptionPane.showMessageDialog(null, "Населений пункт не знайдено", "Помилка", JOptionPane.PLAIN_MESSAGE);
			} catch (HttpStatusException exc2) {
				JOptionPane.showMessageDialog(null, "Населений пункт не знайдено", "Помилка", JOptionPane.PLAIN_MESSAGE);
			} catch (UnknownHostException exc3) {
				JOptionPane.showMessageDialog(null, "З'єднання відсутнє", "Помилка", JOptionPane.PLAIN_MESSAGE);
			} catch (SocketException exc4) {
				JOptionPane.showMessageDialog(null, "З'єднання відсутнє", "Помилка", JOptionPane.PLAIN_MESSAGE);
			} catch (IOException exc5) {
				JOptionPane.showMessageDialog(null, "Населений пункт не знайдено", "Помилка", JOptionPane.PLAIN_MESSAGE);
			} 	
			text="";
			//Цикл отримання прогнозу
			for(int i=1;i<=slider.getValue();i++) {
				text = text+doc.select("div#bd"+Integer.toString(i)).select("p").text()+" - "+doc.select("div#bd"+Integer.toString(i)).select("div").attr("title")+": "+doc.select("div#bd"+Integer.toString(i)).select("div.max").text()+" "+doc.select("div#bd"+Integer.toString(i)).select("div.min").text()+" "+"\n";
			}
		}
		
		public String toString() {
			return "Погода у "+input.getText()+":\n"+text;
		}
	}
	
	
	
	class ButtonCopyEventListener  implements ActionListener { //Клас івент кнопки копіювання
		public void actionPerformed (ActionEvent e) {
			dp.request();
			StringSelection stringSelection = new StringSelection(dp.toString()); //Поміщення прогнозу у буфер обміну
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
		}
	}
	
	class ButtonSearchEventListener implements ActionListener { //Клас івент кнопки пошуку
		public void actionPerformed (ActionEvent e) {
			dp.request();
			JOptionPane.showMessageDialog(null, text, "Погода у "+input.getText(), JOptionPane.PLAIN_MESSAGE);
		}
	} 
	
	class ButtonBrowserEventListener  implements ActionListener { //Клас івент кнопки браузера
		public void actionPerformed (ActionEvent e) {
			String url = "https://ua.sinoptik.ua/";
			
			if (!(input.getText().isEmpty())) {
				url = "https://ua.sinoptik.ua/погода-"+input.getText();
			}
			
			try {
				Desktop.getDesktop().browse(new URL(url).toURI());
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(null, "Помилка", "Помилка", JOptionPane.PLAIN_MESSAGE);
			}
		}
	}
	
	class ButtonFileEventListener implements ActionListener { //Клас івент кнопки створення файлу
		public void actionPerformed (ActionEvent e) {
			try {
				dp.request();
				String path = new File("").getAbsolutePath();
				File file = new File(path + "\\Прогноз "+input.getText()+".txt");
				if(!file.exists()) {
					file.createNewFile();
				}
				FileWriter writer = new FileWriter(file);
				writer.write(dp.toString());
				writer.flush();
				writer.close();
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(null, "Файл не був створений", "Помилка", JOptionPane.PLAIN_MESSAGE);
			}
		}
	}
	
}