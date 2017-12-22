package org.apache;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JWindow;

@SuppressWarnings("serial")
public class GenerateLicense extends JWindow implements Runnable {
	Thread splashThread;
	JProgressBar progress;
	private static final int[] DIGITS_POWER = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000 };
	@SuppressWarnings("unused")
	private static final String NUM_CHAR = "0123456789";
	private static int charLen = "0123456789".length();

	public String generateTOTP512(String seed, String time, String returnDigits) {
		return generateTOTP(seed, time, returnDigits, "HmacSHA512");
	}

	private String generateTOTP(String seed, String time, String returnDigits, String crypto) {
		int codeDigits = Integer.decode(returnDigits).intValue();
		String result = null;
		while (time.length() < 16) {
			time = "0" + time;
		}
		byte[] msg = hexStr2Bytes(time);
		byte[] k = hexStr2Bytes(seed);
		byte[] hash = hmac_sha(crypto, k, msg);
		int offset = hash[(hash.length - 1)] & 0xF;
		int binary = (hash[offset] & 0x7F) << 24 | (hash[(offset + 1)] & 0xFF) << 16 | (hash[(offset + 2)] & 0xFF) << 8
				| hash[(offset + 3)] & 0xFF;
		int otp = binary % DIGITS_POWER[codeDigits];
		result = Integer.toString(otp);
		while (result.length() < codeDigits) {
			result = "0" + result;
		}
		return result;
	}

	private byte[] hexStr2Bytes(String hex) {
		byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();
		byte[] ret = new byte[bArray.length - 1];
		for (int i = 0; i < ret.length; i++)
			ret[i] = bArray[(i + 1)];
		return ret;
	}

	private byte[] hmac_sha(String crypto, byte[] keyBytes, byte[] text) {
		try {
			Mac hmac = Mac.getInstance(crypto);
			SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
			hmac.init(macKey);
			return hmac.doFinal(text);
		} catch (GeneralSecurityException gse) {
			throw new UndeclaredThrowableException(gse);
		}
	}

	public static final int[] produceNumber(int maxLength) {
		Random random = new Random();
		int[] arr = new int[3];
		int num1 = random.nextInt(maxLength);
		arr[0] = num1;
		int num2 = random.nextInt(maxLength);
		while (num2 == arr[0]) {
			num2 = random.nextInt(maxLength);
		}
		arr[1] = num2;
		int num3 = random.nextInt(maxLength);
		while ((num3 == arr[0]) || (num3 == arr[1])) {
			num3 = random.nextInt(maxLength);
		}
		arr[2] = num3;
		return arr;
	}

	public static String upsetStr(String license) {
		StringBuffer sb = new StringBuffer(license.length());
		int[] arr = produceNumber(license.length());
		for (int i = 0; i < license.length(); i++) {
			if ((arr[0] == i) || (arr[1] == i) || (arr[2] == i)) {
				sb.append((char) (int) (Math.random() * 26.0D + 65.0D));
			}
			if (i < license.toCharArray().length) {
				sb.append(license.toCharArray()[i]);
			}
		}
		return sb.toString();
	}

	public static String reverseByArray(String original) {
		return new StringBuffer(original).reverse().toString();
	}

	public static String getRandomNumber(long time, int randomNumberDigit) {
		System.out.println("当前时间:" + time);
		StringBuffer sb = new StringBuffer(String.valueOf(time));
		Random random = new Random();
		System.out.println(random);
		for (int i = 0; i < randomNumberDigit; i++) {
			sb.append("0123456789".charAt(random.nextInt(charLen)));
		}
		return sb.toString();
	}

	public GenerateLicense() {
		Container container = getContentPane();
		setCursor(Cursor.getPredefinedCursor(3));
		URL url = getClass().getResource("");
		if (url != null) {
			container.add(new JLabel(new ImageIcon(url)), "Center");
		}
		this.progress = new JProgressBar(1, 100);
		this.progress.setStringPainted(true);
		this.progress.setString("加载程序中,请稍候......");
		this.progress.setBackground(Color.white);
		container.add(this.progress, "South");

		Dimension screen = getToolkit().getScreenSize();
		pack();
		setLocation((screen.width - getSize().width) / 2, (screen.height - getSize().height) / 2);
	}

	public void start() {
		toFront();
		this.splashThread = new Thread(this);
		this.splashThread.start();
	}

	public void run() {
		setVisible(true);
		try {
			for (int i = 0; i < 100; i++) {
				Thread.sleep(5L);
				this.progress.setValue(this.progress.getValue() + 1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		dispose();
		showFrame();
	}

	static void showFrame() {
		JFrame frame = new JFrame("生成验证码");
		frame.setSize(300, 120);
		JPanel jpn = new JPanel(null);
		JButton btn = new JButton("生成license");
		JTextField field = new JTextField(150);
		btn.setBounds(70, 10, 120, 20);
		field.setBounds(40, 40, 200, 20);

		jpn.add(btn);
		jpn.add(field);
		frame.add(jpn);
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String license = null;
				GenerateLicense cd = new GenerateLicense();

				String seed = "20160315";
				long time = 0L;
				String steps = null;

				time = System.currentTimeMillis();
				String randomTime = GenerateLicense.getRandomNumber(time, 3);
				System.out.println("currentTimeRand:" + randomTime);
				System.out.println("    currentTime:" + time);
				System.out.println(String.valueOf(time).length());
				steps = Long.toHexString(Long.parseLong(randomTime)).toUpperCase();
				String genPwd = cd.generateTOTP512(seed, steps, "8");
				System.out.println(genPwd);
				license = genPwd + GenerateLicense.reverseByArray(String.valueOf(randomTime));
				String real = GenerateLicense.upsetStr(license);
				System.out.println("最终license:" + real);
				field.setText(real);
			}
		});
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screen.width;
		int screenHeight = screen.height;
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setLocation(screenWidth / 2 - frame.getWidth() / 2 - 200, screenHeight / 2 - frame.getHeight() / 2 - 200);
		frame.setDefaultCloseOperation(3);
	}

	public static void main(String[] args) {
		GenerateLicense splash = new GenerateLicense();
		splash.start();
	}
}