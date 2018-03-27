package bot;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

class newGUI extends JFrame {

    final Object lock = new Object();

    newGUI() {
        JLabel label1 = new JLabel();
        JPanel panel1 = new JPanel();
        JLabel label3 = new JLabel();
        JComboBox<String> rockTypes = new JComboBox<>(new String[]{"Clay (west)", "Clay (east)", "Iron (west)", "Iron (east)"});
        JCheckBox enableAntiban = new JCheckBox("", true);
        JCheckBox enableMule = new JCheckBox();
        JCheckBox enableHopWorlds = new JCheckBox();
        JPanel hopSetup = new JPanel();
        JLabel label4 = new JLabel();
        JSpinner hopWhenPlayers = new JSpinner();
        JLabel label5 = new JLabel();
        JSpinner hopMinutes = new JSpinner();
        JLabel label6 = new JLabel();
        JPanel muleSetup = new JPanel();
        JTextField muleUsername = new JTextField();
        JLabel label7 = new JLabel();
        JSpinner resourceLim = new JSpinner();
        JButton startScript = new JButton();

        //======== this ========
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout) contentPane.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
        ((GridBagLayout) contentPane.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        ((GridBagLayout) contentPane.getLayout()).columnWeights = new double[]{0.01, 0.0, 0.01, 0.0, 0.0, 0.0, 1.0E-4};
        ((GridBagLayout) contentPane.getLayout()).rowWeights = new double[]{0.01, 0.01, 0.01, 0.01, 0.0, 0.0, 0.01, 1.0E-4};

        //---- label1 ----
        label1.setText("ClaymOre 1.16");
        label1.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 22));
        contentPane.add(label1, new GridBagConstraints(0, 0, 6, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 5, 0), 0, 0));

        //======== panel1 ========
        {
            panel1.setBorder(new TitledBorder("Options"));

            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[]{0.01, 0.01, 0.01, 0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[]{0.01, 0.01, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

            //---- label3 ----
            label3.setText("Select Rock:");
            label3.setVerticalAlignment(SwingConstants.TOP);
            label3.setLabelFor(rockTypes);
            label3.setToolTipText("The rocks that the bot will focus on");
            panel1.add(label3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 10), 0, 0));
            panel1.add(rockTypes, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 10), 0, 0));

            //---- enableAntiban ----
            enableAntiban.setText("Enable anti-ban");
            enableAntiban.setToolTipText("Enables various methods to replicate being a real player");
            panel1.add(enableAntiban, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 10), 0, 0));

            //---- enableMule ----
            enableMule.setText("Enable mule");
            enableMule.setToolTipText("Automates trading over collected resources to a \"mule\" account");
            panel1.add(enableMule, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 10), 0, 0));

            //---- enableHopWorlds ----
            enableHopWorlds.setText("Hop worlds");
            enableHopWorlds.setToolTipText("Enables world hopping if the constraints are met");
            panel1.add(enableHopWorlds, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 10), 0, 0));
        }
        contentPane.add(panel1, new GridBagConstraints(0, 1, 6, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 5, 0), 0, 0));

        //======== hopSetup ========
        {
            hopSetup.setBorder(new TitledBorder("Hop Constraints"));
            hopSetup.setLayout(new GridBagLayout());
            ((GridBagLayout) hopSetup.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0, 0};
            ((GridBagLayout) hopSetup.getLayout()).rowHeights = new int[]{0, 0, 0, 0};
            ((GridBagLayout) hopSetup.getLayout()).columnWeights = new double[]{0.01, 0.01, 0.01, 0.01, 0.01, 1.0E-4};
            ((GridBagLayout) hopSetup.getLayout()).rowWeights = new double[]{0.01, 0.0, 0.0, 1.0E-4};

            //---- label4 ----
            label4.setText("Hop when");
            label4.setLabelFor(hopWhenPlayers);
            hopSetup.add(label4, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 10), 0, 0));
            hopSetup.add(hopWhenPlayers, new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 10), 0, 0));

            //---- label5 ----
            label5.setText("players every");
            label5.setLabelFor(hopMinutes);
            hopSetup.add(label5, new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 10), 0, 0));
            hopSetup.add(hopMinutes, new GridBagConstraints(3, 0, 1, 2, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 10), 0, 0));

            //---- label6 ----
            label6.setText("minutes");
            label6.setLabelFor(hopMinutes);
            hopSetup.add(label6, new GridBagConstraints(4, 0, 1, 2, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 0), 0, 0));
        }
        contentPane.add(hopSetup, new GridBagConstraints(0, 2, 6, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 5, 0), 0, 0));

        hopSetup.setVisible(false);

        enableHopWorlds.addActionListener(e -> {
            if (enableHopWorlds.isSelected()) {
                hopSetup.setVisible(true);
                pack();
            } else if (!enableHopWorlds.isSelected()) {
                hopSetup.setVisible(false);
                pack();
            }
        });

        //======== muleSetup ========
        {
            muleSetup.setBorder(new TitledBorder("Mule Setup"));
            muleSetup.setLayout(new GridBagLayout());
            ((GridBagLayout) muleSetup.getLayout()).columnWidths = new int[]{0, 0, 0, 0};
            ((GridBagLayout) muleSetup.getLayout()).rowHeights = new int[]{0, 0, 0, 0};
            ((GridBagLayout) muleSetup.getLayout()).columnWeights = new double[]{0.01, 0.01, 0.01, 1.0E-4};
            ((GridBagLayout) muleSetup.getLayout()).rowWeights = new double[]{0.01, 0.0, 0.0, 1.0E-4};

            //---- muleUsername ----
            muleUsername.setText("Mule username");
            muleSetup.add(muleUsername, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 10), 0, 0));

            //---- label7 ----
            label7.setText("Resource Limit:");
            label7.setLabelFor(resourceLim);
            label7.setToolTipText("The amount of resources the worker will collect before muling");
            muleSetup.add(label7, new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 10), 0, 0));

            //---- resourceLim ----
            resourceLim.setToolTipText("The amount of resources the worker will collect before muling");
            muleSetup.add(resourceLim, new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 0), 0, 0));
        }
        contentPane.add(muleSetup, new GridBagConstraints(0, 3, 6, 2, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 5, 0), 0, 0));

        muleSetup.setVisible(false);

        enableMule.addActionListener(e -> {
            if (enableMule.isSelected()) {
                muleSetup.setVisible(true);
                Global.shouldMule(true);
                pack();
            } else if (!enableMule.isSelected()) {
                muleSetup.setVisible(false);
                Global.shouldMule(false);
                pack();
            }
        });

        //---- startScript ----
        startScript.setText("Start");
        contentPane.add(startScript, new GridBagConstraints(0, 5, 6, 2, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        rockTypes.setSelectedIndex(0);
        Global.guiSetOre(rockTypes.getItemAt(rockTypes.getSelectedIndex()));

        rockTypes.addActionListener(e -> {
            String selectedRock = rockTypes.getSelectedItem().toString();
            if (!selectedRock.equals(""))
                Global.guiSetOre(selectedRock);
        });

        Global.setShouldAntiban(true);

        enableAntiban.addActionListener(e -> {
            if (enableAntiban.isSelected()) {
                Global.setShouldAntiban(true);
            } else if (!enableAntiban.isSelected())
                Global.setShouldAntiban(false);
        });

        Global.shouldMule(false);
        resourceLim.setValue(300);
        hopWhenPlayers.setValue(3);
        hopMinutes.setValue(5);

        resourceLim.addChangeListener(e -> {
            int workerLimits = (Integer) resourceLim.getValue();
            if (workerLimits < 0)
                resourceLim.setValue(0);
        });
        hopWhenPlayers.addChangeListener(e -> {
            int hopPlayer = (Integer) hopWhenPlayers.getValue();
            if (hopPlayer < 0)
                hopWhenPlayers.setValue(0);
        });
        hopMinutes.addChangeListener(e -> {
            int hopMins = (Integer) hopMinutes.getValue();
            if (hopMins < 0)
                hopMinutes.setValue(0);
        });
        muleUsername.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (muleUsername.getText().equals("Mule username"))
                    muleUsername.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (muleUsername.getText().equals(""))
                    muleUsername.setText("Mule username");
            }
        });

        startScript.addActionListener(e -> {
            synchronized (lock) {
                lock.notify();
            }
            if (enableMule.isSelected()) {
                Global.setMuleUsername(muleUsername.getText());
                Global.setMuleResourceLim((Integer) resourceLim.getValue());
            }
            if (enableHopWorlds.isSelected())
                Global.setHopValues((Integer) hopWhenPlayers.getValue(), (Integer) hopMinutes.getValue());
            setVisible(false);
        });

        //Enable GUI
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}
