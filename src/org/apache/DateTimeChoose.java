package org.apache;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DateTimeChoose extends JPanel {

	private static final long serialVersionUID = 1L;
	private DateChooser dateChooser = null;
	private JComponent showDate;
	private Date initDate;
	private JP4 jp4;
	private Timer timer;
	private SimpleDateFormat sdf;
	private static String randTime;

	public static DateTimeChoose getInstance() {
		return new DateTimeChoose();
	}

	public static DateTimeChoose getInstance(Date date) {
		return new DateTimeChoose(date);
	}

	public static DateTimeChoose getInstance(String format) {
		return new DateTimeChoose(format);
	}

	public static DateTimeChoose getInstance(Date date, String format) {
		return new DateTimeChoose(date, format);
	}

	/**
	 * Creates a new instance of DateChooser
	 */
	private DateTimeChoose() {
		this(new Date());
	}

	private DateTimeChoose(Date date) {
		this(date, "yyyy年MM月dd日");
	}

	private DateTimeChoose(String format) {
		this(new Date(), format);
	}

	private DateTimeChoose(Date date, String format) {
		initDate = date;
		sdf = new SimpleDateFormat(format);
		// select = Calendar.getInstance();
		// select.setTime(initDate);
		// initPanel();
	}

	@SuppressWarnings("unused")
	private static Date getNowDate() {
		return Calendar.getInstance().getTime();
	}

	@SuppressWarnings("unused")
	private static SimpleDateFormat getDefaultDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	// 覆盖父类的方法使之无效
	public void addActionListener(ActionListener listener) {
	}

	@SuppressWarnings("serial")
	private class DateChooser extends JPanel implements ActionListener, ChangeListener {
		int startYear = 1980; // 默认【最小】显示年份
		int lastYear = 2050; // 默认【最大】显示年份
		int width = 400; // 界面宽度
		int height = 200; // 界面高度

		Color backGroundColor = Color.gray; // 底色
		// 月历表格配色----------------//
		Color palletTableColor = Color.white; // 日历表底色
		Color todayBackColor = Color.orange; // 今天背景色
		Color weekFontColor = Color.blue; // 星期文字色
		Color dateFontColor = Color.black; // 日期文字色
		Color weekendFontColor = Color.red; // 周末文字色

		// 控制条配色------------------//
		Color controlLineColor = Color.pink; // 控制条底色
		Color controlTextColor = Color.white; // 控制条标签文字色

		@SuppressWarnings("unused")
		Color rbFontColor = Color.white; // RoundBox文字色
		@SuppressWarnings("unused")
		Color rbBorderColor = Color.red; // RoundBox边框色
		@SuppressWarnings("unused")
		Color rbButtonColor = Color.pink; // RoundBox按钮色
		@SuppressWarnings("unused")
		Color rbBtFontColor = Color.red; // RoundBox按钮文字色

		JDialog dialog;
		JSpinner yearSpin;
		JSpinner monthSpin;
		JSpinner hourSpin;
		@SuppressWarnings("rawtypes")
		JComboBox minSpin;
		@SuppressWarnings("rawtypes")
		JComboBox secondBox;
		JButton[][] daysButton = new JButton[6][7];

		DateChooser() {
			setLayout(new BorderLayout());
			setBorder(new LineBorder(backGroundColor, 2));
			setBackground(backGroundColor);

			JPanel topYearAndMonth = createYearAndMonthPanal();
			add(topYearAndMonth, BorderLayout.NORTH);
			JPanel centerWeekAndDay = createWeekAndDayPanal();
			add(centerWeekAndDay, BorderLayout.CENTER);
		}

		@SuppressWarnings("rawtypes")
		private JPanel createYearAndMonthPanal() {
			Calendar c = getCalendar();
			int currentYear = c.get(Calendar.YEAR);
			int currentMonth = c.get(Calendar.MONTH) + 1;
			int currentHour = c.get(Calendar.HOUR_OF_DAY);

			JPanel result = new JPanel();
			result.setLayout(new FlowLayout());
			result.setBackground(controlLineColor);

			yearSpin = new JSpinner(new SpinnerNumberModel(currentYear, startYear, lastYear, 1));
			yearSpin.setPreferredSize(new Dimension(48, 20));
			yearSpin.setName("Year");
			yearSpin.setEditor(new JSpinner.NumberEditor(yearSpin, "####"));
			yearSpin.addChangeListener(this);
			result.add(yearSpin);

			JLabel yearLabel = new JLabel("年");
			yearLabel.setForeground(controlTextColor);
			result.add(yearLabel);

			monthSpin = new JSpinner(new SpinnerNumberModel(currentMonth, 1, 12, 1));
			monthSpin.setPreferredSize(new Dimension(35, 20));
			monthSpin.setName("Month");
			monthSpin.addChangeListener(this);
			result.add(monthSpin);

			JLabel monthLabel = new JLabel("月");
			monthLabel.setForeground(controlTextColor);
			result.add(monthLabel);

			hourSpin = new JSpinner(new SpinnerNumberModel(currentHour, 0, 23, 1));
			hourSpin.setPreferredSize(new Dimension(35, 20));
			hourSpin.setName("Hour");
			hourSpin.addChangeListener(this);
			result.add(hourSpin);

			JLabel hourLabel = new JLabel("时");
			hourLabel.setForeground(controlTextColor);
			result.add(hourLabel);

			minSpin = new JComboBox();
			addComboBoxItem(minSpin);
			minSpin.setPreferredSize(new Dimension(45, 20));
			minSpin.setName("Min");
			minSpin.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					JComboBox source = (JComboBox) e.getSource();
					Calendar c = getCalendar();
					if (source.getName().equals("Min")) {
						c.set(Calendar.MINUTE, getSelectedMin());
						setDate(c.getTime());
						return;
					}
				}
			});
			result.add(minSpin);

			JLabel minLabel = new JLabel("分");
			hourLabel.setForeground(controlTextColor);
			result.add(minLabel);

			secondBox = new JComboBox();
			addComboBoxItem(secondBox);
			secondBox.setPreferredSize(new Dimension(45, 20));
			secondBox.setName("Second");
			// secondBox.addActionListener(this) ;
			secondBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					JComboBox source = (JComboBox) e.getSource();
					Calendar c = getCalendar();
					if (source.getName().equals("Second")) {
						c.set(Calendar.SECOND, getSelectedSecond());
						setDate(c.getTime());
						return;
					}
				}
			});
			result.add(secondBox);
			JLabel secondLabel = new JLabel("秒");
			hourLabel.setForeground(controlTextColor);
			result.add(secondLabel);
			return result;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private void addComboBoxItem(JComboBox comboBox) {
			for (int i = 0; i < 60; i++) {
				comboBox.addItem(i);
			}
		}

		private JPanel createWeekAndDayPanal() {
			String colname[] = { "日", "一", "二", "三", "四", "五", "六" };
			JPanel result = new JPanel();
			// 设置固定字体，以免调用环境改变影响界面美观
			result.setFont(new Font("宋体", Font.PLAIN, 12));
			result.setLayout(new GridLayout(7, 7));
			result.setBackground(Color.white);
			JLabel cell;

			for (int i = 0; i < 7; i++) {
				cell = new JLabel(colname[i]);
				cell.setHorizontalAlignment(JLabel.RIGHT);
				if (i == 0 || i == 6)
					cell.setForeground(weekendFontColor);
				else
					cell.setForeground(weekFontColor);
				result.add(cell);
			}

			int actionCommandId = 0;
			for (int i = 0; i < 6; i++)
				for (int j = 0; j < 7; j++) {
					JButton numberButton = new JButton();
					numberButton.setBorder(null);
					numberButton.setHorizontalAlignment(SwingConstants.RIGHT);
					numberButton.setActionCommand(String.valueOf(actionCommandId));
					numberButton.addActionListener(this);
					numberButton.setBackground(palletTableColor);
					numberButton.setForeground(dateFontColor);
					if (j == 0 || j == 6)
						numberButton.setForeground(weekendFontColor);
					else
						numberButton.setForeground(dateFontColor);
					daysButton[i][j] = numberButton;
					result.add(numberButton);
					actionCommandId++;
				}

			return result;
		}

		private JDialog createDialog(Component owner) {
			JDialog result = null;
			if (owner instanceof Frame)
				result = new JDialog((Frame) owner, "日期时间选择", true);
			if (owner instanceof Dialog)
				result = new JDialog((Dialog) owner, "日期时间选择", true);
			result.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			result.addWindowListener(new WindowListener() {
				@Override
				public void windowOpened(WindowEvent e) {
				}

				@Override
				public void windowClosing(WindowEvent e) {
					dialog = null;
					stopUpdateDate();
				}

				@Override
				public void windowClosed(WindowEvent e) {
				}

				@Override
				public void windowIconified(WindowEvent e) {
				}

				@Override
				public void windowDeiconified(WindowEvent e) {
				}

				@Override
				public void windowActivated(WindowEvent e) {
				}

				@Override
				public void windowDeactivated(WindowEvent e) {
				}

			});
			result.getContentPane().add(this, BorderLayout.CENTER);
			jp4 = new JP4();
			result.getContentPane().add(jp4, BorderLayout.SOUTH);
			result.pack();
			result.setSize(width, height);
			return result;
		}

		@SuppressWarnings("deprecation")
		void showDateChooser(Component component, Point position) {
			Component owner = SwingUtilities.getWindowAncestor(component);
			if (dialog == null || dialog.getOwner() != owner)
				dialog = createDialog(owner);
			dialog.setLocation(getAppropriateLocation(owner, position));
			flushWeekAndDay();
			dialog.show();
		}

		Point getAppropriateLocation(Component owner, Point position) {
			Point result = new Point(position);
			Point p = owner.getLocation();
			int offsetX = (position.x + width) - (p.x + owner.getWidth());
			int offsetY = (position.y + height) - (p.y + owner.getHeight());
			if (offsetX > 0) {
				result.x -= offsetX;
			}
			if (offsetY > 0) {
				result.y -= offsetY;
			}
			return result;
		}

		private Calendar getCalendar() {
			Calendar result = Calendar.getInstance();
			result.setTime(getDate());
			return result;
		}

		private int getSelectedYear() {
			return ((Integer) yearSpin.getValue()).intValue();
		}

		private int getSelectedMonth() {
			return ((Integer) monthSpin.getValue()).intValue();
		}

		private int getSelectedHour() {
			return ((Integer) hourSpin.getValue()).intValue();
		}

		private int getSelectedMin() {
			return (Integer) this.minSpin.getSelectedItem();
		}

		private int getSelectedSecond() {
			return (Integer) this.secondBox.getSelectedItem();
		}

		private void dayColorUpdate(boolean isOldDay) {
			Calendar c = getCalendar();
			int day = c.get(Calendar.DAY_OF_MONTH);
			c.set(Calendar.DAY_OF_MONTH, 1);
			int actionCommandId = day - 2 + c.get(Calendar.DAY_OF_WEEK);
			int i = actionCommandId / 7;
			int j = actionCommandId % 7;
			if (isOldDay)
				daysButton[i][j].setForeground(dateFontColor);
			else
				daysButton[i][j].setForeground(todayBackColor);
		}

		private void flushWeekAndDay() {
			Calendar c = getCalendar();
			c.set(Calendar.DAY_OF_MONTH, 1);
			int maxDayNo = c.getActualMaximum(Calendar.DAY_OF_MONTH);
			int dayNo = 2 - c.get(Calendar.DAY_OF_WEEK);
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 7; j++) {
					String s = "";
					if (dayNo >= 1 && dayNo <= maxDayNo)
						s = String.valueOf(dayNo);
					daysButton[i][j].setText(s);
					dayNo++;
				}
			}
			dayColorUpdate(false);
		}

		public void stateChanged(ChangeEvent e) {
			JSpinner source = (JSpinner) e.getSource();
			Calendar c = getCalendar();
			if (source.getName().equals("Hour")) {
				c.set(Calendar.HOUR_OF_DAY, getSelectedHour());
				setDate(c.getTime());
				return;
			}

			dayColorUpdate(true);

			if (source.getName().equals("Year"))
				c.set(Calendar.YEAR, getSelectedYear());
			else
				// (source.getName().equals("Month"))
				c.set(Calendar.MONTH, getSelectedMonth() - 1);
			setDate(c.getTime());
			flushWeekAndDay();
		}

		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			if (source.getText().length() == 0)
				return;
			source.setForeground(todayBackColor);
			Calendar c = getCalendar();
			c.set(Calendar.YEAR, getSelectedYear());
			c.set(Calendar.MONTH, getSelectedMonth() - 1);
			dayColorUpdate(true);
			source.setForeground(todayBackColor);
			int newDay = Integer.parseInt(source.getText());
			c.set(Calendar.DAY_OF_MONTH, newDay);
			c.set(Calendar.HOUR_OF_DAY, getSelectedHour());
			c.set(Calendar.MINUTE, getSelectedMin());
			c.set(Calendar.SECOND, getSelectedSecond());
			setDate(c.getTime());
			flushWeekAndDay();
			if (dialog != null) {
				dialog.setVisible(false);
				dialog = null;
				stopUpdateDate();
			}
		}

	}

	public void setDate(Date date) {
		initDate = date;
		if (showDate instanceof JTextField) {
			((JTextField) showDate).setText(sdf.format(date));
			randTime = sdf.format(date);
		}
	}

	public Date getDate() {
		return initDate;
	}

	public void register(final JComponent showDate) {
		this.showDate = showDate;
		showDate.setRequestFocusEnabled(true);
		showDate.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				showDate.requestFocusInWindow();
			}
		});
		this.setBackground(Color.WHITE);
		this.add(showDate, BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(90, 25));
		this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		showDate.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent me) {
				if (showDate.isEnabled()) {
					showDate.setCursor(new Cursor(Cursor.HAND_CURSOR));
					showDate.setForeground(Color.RED);
				}
			}

			public void mouseExited(MouseEvent me) {
				if (showDate.isEnabled()) {
					showDate.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					showDate.setForeground(Color.BLACK);
				}
			}

			public void mousePressed(MouseEvent me) {
				if (showDate.isEnabled()) {
					showDate.setForeground(Color.CYAN);
					showPanel(showDate);
				}
			}

			public void mouseReleased(MouseEvent me) {
				if (showDate.isEnabled()) {
					showDate.setForeground(Color.BLACK);
				}
			}
		});

		showDate.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				// hidePanel();
			}

			public void focusGained(FocusEvent e) {
			}
		});
	}

	private void showPanel(Component owner) {
		if (dateChooser == null)
			dateChooser = new DateChooser();

		Point show = new Point(0, showDate.getHeight());
		SwingUtilities.convertPointToScreen(show, showDate);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		int x = show.x;
		int y = show.y;
		if (x < 0) {
			x = 0;
		}
		if (x > size.width - 295) {
			x = size.width - 295;
		}

		if (y < size.height - 170) {
		} else {
			y -= 188;
		}
		dateChooser.showDateChooser(owner, show);
		updateDate();
	}

	public void stopUpdateDate() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (jp4 != null) {
			jp4 = null;
		}
		System.gc();
	}

	private class JP4 extends JPanel {
		private static final long serialVersionUID = -6391305687575714469L;
		final JLabel jl;

		public JP4() {
			super(new BorderLayout());
			this.setPreferredSize(new Dimension(295, 20));
			this.setBackground(Color.pink);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
			jl = new JLabel("今天: " + sdf.format(new Date()));
			jl.setToolTipText("点击选择今天日期");
			this.add(jl, BorderLayout.CENTER);
			jl.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent me) {
					jl.setCursor(new Cursor(Cursor.HAND_CURSOR));
					jl.setForeground(Color.RED);
				}

				public void mouseExited(MouseEvent me) {
					jl.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					jl.setForeground(Color.BLACK);
				}

				public void mousePressed(MouseEvent me) {
					jl.setForeground(Color.WHITE);
					setDate(new Date());
					if (dateChooser.dialog != null) {
						dateChooser.dialog.setVisible(false);
						dateChooser.dialog = null;
						stopUpdateDate();
					}
				}

				public void mouseReleased(MouseEvent me) {
					jl.setForeground(Color.BLACK);
				}

			});
			updateDate();
		}
	}

	private void updateDate() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				if (jp4 != null) {
					jp4.jl.setText("今天: " + sdf.format(new Date()));
				} else {
					if (timer != null) {
						timer.cancel();
					}
				}
			}
		}, 0, 1000);
	}

	public static void main(String[] args) {
		System.setProperty("user.timezone","Asia/Shanghai");
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
		DateTimeChoose dateChooser1 = DateTimeChoose.getInstance("yyyy-MM-dd HH:mm:ss");
		JTextField showDate1 = new JTextField("单击选择日期");
		dateChooser1.register(showDate1);
		JFrame jf = new JFrame("license生成器");
		jf.setLayout(null);
		JButton btn = new JButton("生成license");
		JTextField field = new JTextField(150);
		field.setBounds(15, 245, 300, 20);
		btn.setBounds(212, 15, 110, 20);
		jf.add(btn);
		jf.add(field);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("----" + randTime);

				String license = null;
				String seed = "20160315";
				long time = 0L;
				String steps = null;
				
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					Date dateEnding = df.parse(randTime);
					time = dateEnding.getTime();
		            GenerateLicense ge = new GenerateLicense();
		            String randomTime = GenerateLicense.getRandomNumber(time, 3);
		            steps = Long.toHexString(Long.parseLong(randomTime)).toUpperCase();
		            String genPwd = ge.generateTOTP512(seed, steps, "8");
		            System.out.println(genPwd);
		            license = genPwd + GenerateLicense.reverseByArray(String.valueOf(randomTime));
		            String real = GenerateLicense.upsetStr(license);
		            System.out.println("最终license:" + real);
		            field.setText(real);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		jf.setBounds(400, 200, 400, 300);
		jf.add(showDate1, BorderLayout.NORTH);
		showDate1.setBounds(10, 10, 200, 30);
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
		jf.setResizable(false);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}