package start;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;


public class Main



    /**
     * 顶部panel设置方法 传参是一个panel，其实不必要，因为顶部panel是可以直接访问到的
     * 该方法会对传入的panel进行设置，刷新
     *
     * @param panel
     */
    public void setShowPanel(JScrollPane panel) {
        panel = panel == null ? new JScrollPane() : panel;   //如果传入的panel是一个空值，没有对象，那么new一个对象，其实不必要，因为我提前声明过，不会传入空值
        panel.getViewport().setVisible(false);               //让面板暂时关闭
        panel.getViewport().removeAll();                     //至于为什么，因为不关闭，removall出问题，我不会别的方法，就这么写了，
        panel.getViewport().setVisible(true);                // 清除完毕之后再给他显示出来，然后下面给他添加东西了。所以这个方法不仅可用于第一次构造顶部面板，还可以用来刷新面板上面的显示内容
        String[] tableHeader = {"编号", "姓名", "性别", "年龄", "学号", "学校", "班级"};//这是table的表头，存成数组免得一个一个写，省事
        DefaultTableModel model = new DefaultTableModel();    // 造一个model，然后用这个model构造表格
        JTable table = new JTable(model);                      //别问我为什么这样，我抄来的
        for (String val : tableHeader) {                        //遍历刚才的表头的数组，诸葛添加表头的列
            model.addColumn(val);
        }
        /**
         * 这个地方开始遍历 student列表就是list逐个添加学生信息到表格里面
         */
        int i = 0;
        for (Student student : tableList) {
            i++;
            model.addRow(new Object[]{
                    i + " /" + tableList.size(),
                    student.getName(),
                    student.getGender(),
                    student.getAge(),
                    student.getId(),
                    student.getSchool(),
                    student.getStudentClass(),
            });
        }
        /**
         * 为表格添加监听，使它可以响应点击，在点击对应的表格以后，将该表格里面的学生信息，存到刚开始那个用于显示顶部面板的信息的对象里面。就是开头Main里面new 的那个Student
         */
        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            // 重写 事件方法
            @Override
            public void valueChanged(ListSelectionEvent e) {
                System.out.println("选中！行： " + table.getSelectedRow());              // 控制台小小的输出一下，有助于我调试嘛
                System.out.println("选中！列： " + table.getSelectedColumn());
                int row = table.getSelectedRow();
                student = tableList.get(row);
                setInsertPanel(insertPanel);
            }
        });
        panel.getViewport().add(table);
    }

    public void setInsertPanel(JPanel panel) {
        panel = panel == null ? new JPanel() : panel;    // 和上面一样的操作了，应该知道什么意思了
        panel.setVisible(false);
        panel.removeAll();
        panel.setVisible(true);
        /**
         * new 一堆要用的组件，把所有用到的东西在开头new出来声明一下是个好习惯。。。。真的
         * 帮同学写c的时候，呗vc逼出来的
         * 不过这样仿佛看上去更好看呢
         * 变量名应该比较清楚，不注释了
         */
        JButton buttonInsert;
        JButton buttonSave;
        JButton buttonDelete;
        JButton buttonSave2File;
        JButton buttonOpenFromFile;
        JButton buttonSearch = new JButton("查询");
        JButton buttonShowAll = new JButton("显示全部");
        JLabel nameLabel = new JLabel("姓名:");
        JLabel genderLabel = new JLabel("性别:");
        JLabel birthLabel = new JLabel("出生日期:");
        JLabel yearLabel = new JLabel("年");
        JLabel monthLabel = new JLabel("月");
        JLabel dayLabel = new JLabel("日");
        JLabel idLabel = new JLabel("学号：");
        JLabel classLabel = new JLabel("班级：");
        JLabel schoolLabel = new JLabel("学校：");
        JComboBox genderBox = new JComboBox();
        JComboBox ageYearBox = new JComboBox();
        JComboBox monthBox = new JComboBox();
        JComboBox dayBox = new JComboBox();
        JTextField nameTextField = new JTextField(8);
        JTextField idTextField = new JTextField(10);
        JTextField classTextField = new JTextField(10);
        JTextField schoolTextField = new JTextField(15);
        JPanel birthPanel = new JPanel();
        JLabel searchIdLabel = new JLabel("学号:");
        JTextField searchIdTextField = new JTextField(18);

        //新建表格包布局，记住表格包布局，
        GridBagLayout bagLayout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();//这个应该是布局管理器吧，我也不知道叫什么，知道功能就好了
        panel.setLayout(bagLayout);                                //将这个panel设置为刚才新建的表格包布局管理器，这个地方原来有更好的注释，被我删了，我现在说不清楚，不准确
        constraints.fill = GridBagConstraints.BOTH;                 //设置填充方式，，，，，
        constraints.weightx = 0.0;

        birthPanel.setLayout(bagLayout);                        /**这个地方要记住，出生日期那一栏里面的组件时单独创建一个面板装起来，在放到顶部面板的，就是现在这个**/

        constraints.gridx = 2;      //这个参数是设置组件左上角在表格里面从低级行开始
        constraints.gridy = 0;      //左上角从哪一列开始
        constraints.gridwidth = 3;  //组件的宽度，单位是 格 表明占用几格
        panel.add(nameLabel, constraints);//使用刚刚弄好的布局将 名字标签添加到面板咯
/**
 * 一下重复很多次，都是一样的擦欧总，知道把所有东西全部填进去
 * 但是日期选择框部分有点特别
 */
        constraints.gridx = 6;
        constraints.gridwidth = 9;
        panel.add(nameTextField, constraints);

        constraints.gridx = 0;
        constraints.gridwidth = 4;
        birthPanel.add(birthLabel, constraints);

        /**
         * 这是年份选择框，Calender获取当前年份，然后从当前年份开始添加选项，知道1900年。这不是重点
         * 看200行
         */
        for (int i = Calendar.getInstance().get(Calendar.YEAR); i > 1900; i--) {
            ageYearBox.addItem(i);
        }
        ageYearBox.setSelectedItem(2000);
        constraints.gridx = 5;
        constraints.gridwidth = 4;
        birthPanel.add(ageYearBox, constraints);

        constraints.gridx = 10;
        constraints.gridwidth = 4;
        birthPanel.add(yearLabel, constraints);

        for (int i = 1; i < 13; i++) {
            monthBox.addItem(i);
        }
        monthBox.setSelectedItem(1);
        constraints.gridx = 15;
        constraints.gridwidth = 2;
        birthPanel.add(monthBox, constraints);

        constraints.gridx = 18;
        constraints.gridwidth = 4;
        birthPanel.add(monthLabel, constraints);

        /**
         * 这是天数选择框，因为涉及到 闰年的二月，还有每个月天数不一样多，
         * 所以要添加监听，在年份和月份改变的时候，自动根据已选择的年月计算出该月有几天，
         * 设置多少个选项
         * 设置监听在后面
         */
        for (int i = 1; i < 32; i++) {
            dayBox.addItem(i);
        }
        dayBox.setSelectedItem(1);
        constraints.gridx = 23;
        constraints.gridwidth = 2;
        birthPanel.add(dayBox, constraints);

        constraints.gridx = 26;
        constraints.gridy = 0;
        constraints.gridwidth = 0;
        birthPanel.add(dayLabel, constraints);

        constraints.gridx = 15;
        constraints.gridy = 0;
        constraints.gridwidth = 29;
        panel.add(birthPanel, constraints);

        constraints.gridx = 45;
        constraints.gridwidth = 1;
        panel.add(new JPanel(), constraints);

        constraints.gridx = 46;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        panel.add(genderLabel, constraints);

        genderBox.addItem("男");
        genderBox.addItem("女");
        genderBox.setSelectedItem("男");
        constraints.gridx = 50;
        constraints.gridy = 0;
        constraints.gridwidth = 0;
        panel.add(genderBox, constraints);

        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.gridwidth = 3;
        panel.add(schoolLabel, constraints);

        constraints.gridx = 6;
        constraints.gridwidth = 15;
        panel.add(schoolTextField, constraints);

        constraints.gridx = 22;
        constraints.gridwidth = 3;
        panel.add(classLabel, constraints);

        constraints.gridx = 26;
        constraints.gridwidth = 15;
        panel.add(classTextField, constraints);

        constraints.gridx = 42;
        panel.add(idLabel, constraints);

        constraints.gridx = 50;
        panel.add(idTextField, constraints);


        constraints.gridy = 1;
        constraints.gridheight = 1;
        panel.add(new JPanel(), constraints);
        constraints.gridy = 3;
        panel.add(new JPanel(), constraints);

        constraints.gridy = 9;
        buttonInsert = new JButton("插入");
        buttonInsert.setBackground(Color.white);
        buttonSave = new JButton("保存");
        buttonSave.setBackground(Color.white);
        buttonDelete = new JButton("删除");
        buttonDelete.setBackground(Color.white);
        buttonSave2File = new JButton("保存到文件");
        buttonSave2File.setBackground(Color.white);
        buttonOpenFromFile = new JButton("从文件打开");
        buttonOpenFromFile.setBackground(Color.white);


        /**
         * 再次注意，底部的插入和保存按钮也是先添加到一个panel，再将panel添加到顶部面板
         * 直到310行都是添加那一行按钮
         */
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buttonInsert);
        buttonPanel.add(buttonSave);
        buttonPanel.add(buttonDelete);
        buttonPanel.add(buttonSave2File);
        buttonPanel.add(buttonOpenFromFile);

        constraints.gridy = 5;
        constraints.gridx = 2;
        panel.add(buttonPanel, constraints);

        constraints.gridy = 7;
        panel.add(new JPanel(), constraints);
        constraints.gridx = 2;
        constraints.gridy = 8;
        constraints.gridwidth = 3;
        panel.add(searchIdLabel, constraints);

        constraints.gridx = 6;
        constraints.gridwidth = 9;
        panel.add(searchIdTextField, constraints);

        /**
         * 下面是添加最下面那个搜索框和搜索按钮
         * 直到322行
         */
        constraints.gridx = 16;
        constraints.gridwidth = 0;                  //前面忘了说，这个宽度如果设置为0，那么就等于 告诉他，这个组件要宽度直到末尾
        buttonSearch.setBackground(Color.lightGray);//设置背景颜色
        buttonShowAll.setBackground(Color.lightGray);
        JPanel searchPanel = new JPanel();
        searchPanel.add(buttonSearch);
        searchPanel.add(buttonShowAll);
        panel.add(searchPanel, constraints);

        //这两行个按钮是保存和删除，所以只有在点击了下面的表格框里面的信息，上面需要更新才应该显示出来
        buttonDelete.setVisible(false);
        buttonSave.setVisible(false);

        //保险起见，吧这些内容都变成长度为0的字符串，避免出现空指针
        //因为我没有细究 会不会出现空指针 暂且这么写
        //是对于第一次启动时的初始化
        nameTextField.setText("");
        genderBox.setSelectedItem("");
        schoolTextField.setText("");
        classTextField.setText("");
        idTextField.setText("");

        /**
         * 这个里面getNUllitem是一个判断student对象里面有没有空白项的方法
         * 如果没有，会返回空字符串，有，会返回空白的字段名称
         */
        if (student.getNullItem().length() == 0) {  //等于0 代表没有为空的，所以当前顶部panel的学生对象里不是空，不是第一次程序启动或者插入完毕刷新。是点击了下面表格的学生，需要及那个学生信息刷新到顶部panel
            // 下面将学神信息刷新到对应的框里面
            nameTextField.setText(student.getName());
            genderBox.setSelectedItem(student.getGender());
            schoolTextField.setText(student.getSchool());
            classTextField.setText(student.getStudentClass());
            idTextField.setText(student.getId());
            ageYearBox.setSelectedItem(student.getBirthDate().getY());
            monthBox.setSelectedItem(student.getBirthDate().getM());
            dayBox.setSelectedItem(student.getBirthDate().getD());
            buttonDelete.setVisible(true);
            buttonSave.setVisible(true);
            buttonInsert.setVisible(false);
        }

        /**
         * 开始设置监听啦啦啦
         * 将监听写成一个内部类，避免重复，因为我发现 “年” 和 “月” 的选择框需要做的操作是一样的
         * 都是根据当前选择日期 刷新“天 ”选择框的选择项
         */
        class boxActionListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                int m = (int) monthBox.getSelectedItem(); // 获取当前选择的年份
                int y = (int) ageYearBox.getSelectedItem();//获取月份
                Calendar calendar = Calendar.getInstance();  // 根据年份和月份 利用Calender得到该月有几天
                calendar.set(y - 1900, m - 1, 1);
                calendar.roll(Calendar.DATE, -1);
                int max = calendar.get(Calendar.DATE);
                dayBox.removeAllItems();                // 将“天”选项框里面的内容圈闭移除，再添加
                for (int i = 1; i <= max; i++) {        //添加
                    dayBox.addItem(i);
                }
            }
        }
        ageYearBox.addActionListener(new boxActionListener());//为 年份选择框添加监听
        monthBox.addActionListener(new boxActionListener());//为 月份选择框添加监听
        // 天选择框 不需要监听 之前写了，发现累赘，删了

        //为按钮设置监听，因为几个按钮操作不一样，设置按钮判断也不是很方便，所以就不写成一个类了
        /**
         * 插入按钮的监听
         */
        buttonInsert.addActionListener(new ActionListener() {
            //冲刺额 监听方法
            @Override
            public void actionPerformed(ActionEvent e) {
                //当用户点击该按钮，吧每一个框里面的信息都收集起来
                student.setName(nameTextField.getText());
                student.setBirth((int) ageYearBox.getSelectedItem(), (int) monthBox.getSelectedItem(), (int) dayBox.getSelectedItem());
                student.setId(idTextField.getText());
                student.setGender((String) genderBox.getSelectedItem());
                student.setSchool(schoolTextField.getText());
                student.setStudentClass(classTextField.getText());
                student.setSchool(schoolTextField.getText());
                student.setNumLock(STUDENTS.size() + 1);
                // 还记得 getNullItem这个方法么
                //如果有字段为空，弹窗显示不可为空
                if (student.getNullItem().length() != 0) {
                    JOptionPane.showMessageDialog(null, "请输入：" + student.getNullItem() + "字段", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //getByid可根据学号查找现有学生，如果找到了，弹窗提示要不要覆盖
                //不对，你的可以直接修改，没有覆盖选项，报错已存在
                if (getStudentByID(student.getId()) != null) {
                    JOptionPane.showMessageDialog(null, "ID 已经存在！", "Error", JOptionPane.ERROR_MESSAGE);
                    return;     //结束操作
                }
                //如果很顺利到了这里
                //那么把当前学生添加到怕列表吧
                STUDENTS.add(student);
                //添加完了，你不会要我手动全部删了吧
                //所以刷新一下  还记得上面刷新那里么
                //如果不重新new 一个学生类，就会呗上面的set方法当作显示刷新，显示出来就不会清除
                student = new Student();
                //调用这个方法重新设置面板，emm，这个方法就是它本身
                setInsertPanel(insertPanel);
                //插入完毕当然也要重新设置下面的表格让他显示出来
                setShowPanel(showPanel);
                //把列表输出一下，方便调试，可以删除
                System.out.println(STUDENTS.toString());
            }
        });

        /**
         * 保存按钮的监听
         */
        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //上面一样的操作
                student.setName(nameTextField.getText());
                student.setBirth((int) ageYearBox.getSelectedItem(), (int) monthBox.getSelectedItem(), (int) dayBox.getSelectedItem());
                student.setId(idTextField.getText());
                student.setGender((String) genderBox.getSelectedItem());
                student.setSchool(schoolTextField.getText());
                student.setStudentClass(classTextField.getText());
                student.setSchool(schoolTextField.getText());
                if (student.getNullItem().length() != 0) {
                    JOptionPane.showMessageDialog(null, "请输入：" + student.getNullItem() + "字段", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Student flag = getStudentByID(student.getId());
                if (flag != null && flag.getNumLock() != student.getNumLock()) {
                    JOptionPane.showMessageDialog(null, "ID 已经存在！", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //保存当前信息咯，因为保存学号肯定是重复的，但是，又不能排除 万一它修改了学号正好与其他同学相同了，所以我加了一个numLock变量用来标记当前操作学生
                //详见下面updateStudentList(student);这个方法的实现
                updateStudentList(student);
                student = new Student();
                setInsertPanel(insertPanel);
                setShowPanel(showPanel);
                System.out.println(STUDENTS.toString());
            }
        });

        /**
         * 删除按钮的监听
         */
        buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //删除按钮只需要得到想删除的id就好啦，所以其他信息都可以不要啦
                student.setId(idTextField.getText());
                int r = JOptionPane.showConfirmDialog(null, "确认删除？" + student.getId(), "警告", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.NO_OPTION) {
                    return;
                }
                Student flag = getStudentByID(student.getId());
                if (flag != null && flag.getNumLock() != student.getNumLock()) {
                    JOptionPane.showMessageDialog(null, "ID 已经存在！", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //删除方法进行删除
                deleteStudentList(student.getNumLock());
                student = new Student();
                setInsertPanel(insertPanel);
                setShowPanel(showPanel);
                System.out.println(STUDENTS.toString());
            }
        });

        /**
         *从文件打开按钮的监听
         */
        buttonOpenFromFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //交给下面这个函数去做，它只要监听就好啦
                openFromFile();
            }
        });
        /**
         *保存到文件按钮的监听
         */
        buttonSave2File.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //交给下面这个函数去做，它只要监听就好啦
                save2file();
            }
        });

        /**
         *搜索按钮的监听
         */
        buttonSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {、
                /**
                 * tablelist呢是用来显示下面表格信息的list
                 * 正常情况下，tablelist等于studentlist
                 * 但是又查找是例外，查找只显示符合条件的，但是不能删除studentlist里面的信息，只好这样啦
                 */
                tableList = searchByID(searchIdTextField.getText());
                setShowPanel(showPanel);
            }
        });

        /**
         * 显示所有信息按钮的监听
         */
        buttonShowAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //显示全部信息嘛，只要将tablelist设置为和studentlist一样，再刷新线面的table 不就一样了嘛
                tableList = STUDENTS;
                setShowPanel(showPanel);
            }
        });
    }

    /**
     * 用id查找方法
     *
     * @param id 传入id的至
     * @return 返回学生对象，没找到返回null
     * 为什么要用列表呢，因为我得是筛选，可能又多个学生
     * 这个呢，如果改的话，那shuaxintable那里也要改，就这么着了
     */
    public List<Student> searchByID(String id) {
        List<Student> list = new ArrayList<Student>();
        if (id.length() == 0) {
            return list;
        }
        for (Student val : STUDENTS) {
            if (id.equals(val.getId())) {
                list.add(val);
            }
        }
        return list;
    }

    /**
     * 从文件打开方法，打开文件
     */
    public void openFromFile() {
        String filePath = chooseFile();//用choosefile方法选择一个文件
        if (filePath.length() == 0) {   //等于0九四没选成功，取消操作，所以结束
            return;
        }
        File file = new File(filePath); //新建一个File对象用于文件操作
        if (!file.exists()) {   //判断文件是否存在
            JOptionPane.showMessageDialog(null, "文件不存在或无权限！", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            StringBuffer XMLBuffer = new StringBuffer();
            Scanner input = new Scanner(file); //把文件读到stringbuffer里面，变成字符串，本来直接用数据流操作更简便，但是我为了调试还有可以分开来用，加了字符串转换的步骤，数据流先转字符串，再转数据流，
            while (input.hasNextLine()) {         //顺着读取，直到读完整个文件
                XMLBuffer.append(input.nextLine());
            }
            关闭文件
            input.close();              //关闭文件
            STUDENTS = XML2list(XMLBuffer.toString());  //这个方法将转换成学生列表对象
            tableList = STUDENTS;                       //读入了数据，下面表格显示内容也要刷新嘛，所以tablelist也操作一下
            setShowPanel(showPanel);                    //刷新一下下面的table，上面？ 上面什么都不用操作嘛
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save2file() {
        String filePath = chooseFile();//用choosefile方法选择一个文件
        if (filePath.length() == 0) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {//判断文件是否存在
            try {
                System.out.println(filePath);
                file.createNewFile();       //文件不存在，创建新文件
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "文件创建失败！", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        String writeString = list2XML(STUDENTS);        //把学生列表转为字符串，同样的，本来可以直接写到文件里面。
        if (file.canWrite()) {
            OutputStream stream = null;
            try {
                stream = new FileOutputStream(file);        //呢哇一个新的输出流，别问为什么，网上抄的，没有为什么
                stream.write(writeString.getBytes("UTF8"));//现在才把它写到文件里面
                JOptionPane.showMessageDialog(null, "写入成功！", "SUCCESS", JOptionPane.ERROR_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "写入失败！", "ERROR", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private String chooseFile() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT文件", "txt", "TXT");//新建文件筛选器对象 第一个参数是显示的文字，第二三个参数是选择的后缀名
        JFileChooser fc = new JFileChooser();           //用filechooser
        fc.setFileFilter(filter);                          //设置文件筛选器
        fc.setMultiSelectionEnabled(false);                 //设置不可以多选
        int result = fc.showSaveDialog(null);               //获取选择结果
        if (result == JFileChooser.APPROVE_OPTION) {        //成功，返回结果
            File file = fc.getSelectedFile();
            return file.getPath();
        }
        //被取消了或者失败
        JOptionPane.showMessageDialog(null, "取消！", "失败", JOptionPane.ERROR_MESSAGE);
        return "";
    }

    public void deleteStudentList(int numLock) {
        /**、
         * 这个代码很有意思，以前我不会用这个筛选器，要自己写一个遍历来操作
         * 如果满足后面这个条件的就会被删除 numlock是唯一的，但是不显示
         */
        STUDENTS.removeIf(s -> s.getNumLock() == numLock);
    }

    public void updateStudentList(Student student) {
        Student temp = null;
        for (Student val : STUDENTS) {
            if (val.getNumLock() == student.getNumLock()) {
                temp = val;
            }
        }
        if (temp != null) {
            temp = new Student(student);
            temp.setNumLock(student.getNumLock());
        }
    }

    public Student getStudentByID(String id) {
        if (id.length() == 0) {
            return null;
        }
        for (Student val : STUDENTS) {
            if (val.getId().equals(id)) {
                return val;
            }
        }
        return null;
    }

    /**
     * list转换为xml文件的代码
     *
     * @param list
     * @return
     */
    public static String list2XML(List<Student> list) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        org.w3c.dom.Document document = builder.newDocument();//创建一个Document对象
        document.setXmlStandalone(true);
        Element student = document.createElement("student");     //给他添加一个根节点
        for (Student val : list) {                                 //遍历list所有学生对象，依次添加为子节点
            Element id = document.createElement("studentID");
            id.setAttribute("id", val.getId());
            Element name = document.createElement("name");
            name.setTextContent(val.getName());
            id.appendChild(name);
            Element gender = document.createElement("gender");
            gender.setTextContent(val.getGender());
            id.appendChild(gender);
            Element school = document.createElement("school");
            school.setTextContent(val.getSchool());
            id.appendChild(school);
            Element birth = document.createElement("birth");
            birth.setTextContent(val.getBirth());
            id.appendChild(birth);
            Element studentClass = document.createElement("class");
            studentClass.setTextContent(val.getStudentClass());
            id.appendChild(studentClass);
            student.appendChild(id);
        }
        document.appendChild(student);//将这些子节点都添加到根节点下面

        /**
         * 下面开始转换为xml
         */

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        TransformerFactory transformerFactory = TransformerFactory.newInstance(); //创建这个什么工厂对象
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");        //好像是设置缩进什么的，忘了，网上抄的
            transformer.transform(new DOMSource(document), new StreamResult(outStream));  //转换吧，具体源代码没看不知道，能用就成
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        System.out.println(outStream.toString());
        return outStream.toString();
    }


    /**
     * 一样的操作，将xml转换为list
     *
     * @param bf
     * @return
     */
    public static List<Student> XML2list(String bf) {
        List<Student> list = new ArrayList<Student>();
        Document document = null;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            StringBuffer sb = new StringBuffer();
            sb.append(bf);
            InputStream inputStream = new ByteArrayInputStream(bf.getBytes("UTF8"));
            document = documentBuilder.parse(inputStream);
            NodeList students = document.getElementsByTagName("studentID");
            for (int i = 0; i < students.getLength(); i++) {
                Node node = students.item(i);
                System.out.println("find ID:" + node.getAttributes().getNamedItem("id").getNodeValue());
                Student retStudent = new Student();
                retStudent.setId(node.getAttributes().getNamedItem("id").getNodeValue());
                NodeList childNodes = node.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    String name = "";
                    String nodeVal = "";
                    if (childNodes.item(j).hasChildNodes()) {
                        name = childNodes.item(j).getNodeName();
                        nodeVal = childNodes.item(j).getFirstChild().getNodeValue();
                    }
                    switch (name) {
                        case "name":
                            retStudent.setName(nodeVal);
                            break;
                        case "gender":
                            retStudent.setGender(nodeVal);
                            break;
                        case "school":
                            retStudent.setSchool(nodeVal);
                            break;
                        case "birth":
                            retStudent.setBirth(nodeVal);
                            break;
                        case "class":
                            retStudent.setStudentClass(nodeVal);
                            break;
                        default:
                            System.out.println("unExpect value:" + name);
                    }

                }
                list.add(retStudent);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        Main gui = new Main();
    }
}{
}


/**
 * 下面是学生类，没什么好说的，一堆get set方法而已
 */

class Student {
    private MyDate birth;
    private String name;
    private String id;
    private String gender;
    private String school;
    private String studentClass;
    private int numLock;

    public Student() {
    }

    public Student(Student student) {
        this.birth = new MyDate(student.getBirthDate());
        this.name = new String(student.getName());
        this.id = new String(student.getId());
        this.gender = new String(student.getGender());
        this.school = new String(student.getSchool());
        this.studentClass = new String(student.getStudentClass());
        this.numLock = student.getNumLock();
    }

    public String getNullItem() {
        if (this.getGender() == null || this.getGender().replace(" ", "").length() == 0) {
            return "性别";
        } else if (this.getSchool() == null || this.getSchool().replace(" ", "").length() == 0) {
            return "学校";
        } else if (this.getName() == null || this.getName().replace(" ", "").length() == 0) {
            return "姓名";
        } else if (this.getId() == null || this.getId().replace(" ", "").length() == 0) {
            return "学号";
        } else {
            return "";
        }
    }

    public void setNumLock(int numLock) {
        this.numLock = numLock;
    }

    public int getNumLock() {
        return this.numLock;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public String getSchool() {
        return school;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getGender() {
        return gender;
    }

    public String getBirth() {
        return this.birth == null ? "null" : this.birth.toString();
    }

    public MyDate getBirthDate() {
        return this.birth;
    }

    public void setBirth(String s) {
        int yIndex = s.indexOf("y=");
        int mIndex = s.indexOf("m=");
        int dIndex = s.indexOf("d=");
        int rIndex = s.indexOf('}');
        try {
            this.birth = new MyDate(
                    Integer.valueOf(s.substring(yIndex + 2, mIndex - 2)),
                    Integer.valueOf(s.substring(mIndex + 2, dIndex - 2)),
                    Integer.valueOf(s.substring(dIndex + 2, rIndex))
            );
        } catch (Exception e) {
            System.out.println("生日构造失败：" + s + "\n" + e.getMessage());
        }

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public void setBirth(int y, int m, int d) {
        this.birth = new MyDate(y, m, d);
    }


    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        return (this.birth.m - m < 0) ? (y - this.birth.y - 1) : y - this.birth.y;
    }

    @Override
    public String toString() {
        return "Student{" +
                "birth=" + birth +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", gender='" + gender + '\'' +
                ", school='" + school + '\'' +
                ", studentClass='" + studentClass + '\'' +
                ", numLock=" + numLock +
                '}';
    }
}

/**
 * mydate方法，也是一堆get set而已，自己写的的日期类
 */
class MyDate {
    int y;
    int m;
    int d;

    MyDate(MyDate birthDate) {
        this(1970, 1, 1);
    }

    MyDate(int y, int m, int d) {
        this.y = y;
        this.m = m;
        this.d = d;
    }

    @Override
    public String toString() {
        return "MyDate{" +
                "y=" + y +
                ", m=" + m +
                ", d=" + d +
                '}';
    }

    public int getD() {
        return d;
    }

    public int getM() {
        return m;
    }

    public int getY() {
        return y;
    }
}

