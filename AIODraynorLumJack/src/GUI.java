package AIODraynorLumJack.src;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
/*
 * Created by JFormDesigner on Wed Dec 28 19:11:04 PST 2016
 */

class GUI extends JFrame {
    final Object lock = new Object();

    GUI() {
        initComponents();
    }

    private void initComponents() {
        JLabel label1 = new JLabel();
        JPanel panel1 = new JPanel();
        JLabel label2 = new JLabel();
        JComboBox<String> treeTypes = new JComboBox<>(new String[]{"Tree", "Oak", "Willow", "Yew"});
        JCheckBox enableHopWorlds = new JCheckBox();
        JCheckBox powerChop = new JCheckBox();
        JCheckBox progressiveMode = new JCheckBox();
        JCheckBox enableAntiban = new JCheckBox("", true);
        JPanel hopSetup = new JPanel();
        JLabel label3 = new JLabel();
        JSpinner playerLim = new JSpinner();
        JLabel label4 = new JLabel();
        JSpinner hopInterval = new JSpinner();
        JLabel label5 = new JLabel();
        JPanel progressiveModeOptions = new JPanel();
        JCheckBox sellLogsAtGE = new JCheckBox();
        JCheckBox skipWillowTrees = new JCheckBox("", true);
        JButton startScript = new JButton();

        //======== this ========
        setTitle("AIO Draynor Lumberjack v1.0");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout) contentPane.getLayout()).columnWidths = new int[]{389, 0};
        ((GridBagLayout) contentPane.getLayout()).rowHeights = new int[]{38, 0, 0, 0, 0, 0};
        ((GridBagLayout) contentPane.getLayout()).columnWeights = new double[]{0.0, 1.0E-4};
        ((GridBagLayout) contentPane.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

        //---- label1 ----
        label1.setText("AIO Draynor Lumberjack");
        label1.setFont(new Font("Dialog", Font.BOLD, 22));
        label1.setToolTipText("DEVASTATE THE RUNESCAPE ENVIRONMENT! You deserve it.");
        contentPane.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 0), 0, 0));

        //======== panel1 ========
        {
            panel1.setBorder(new TitledBorder("Options"));

            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[]{55, 0, 109, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[]{0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 1.0E-4};

            //---- label2 ----
            label2.setText("Select Tree:");
            label2.setToolTipText("What tree should the script start with?");
            panel1.add(label2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));
            panel1.add(treeTypes, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- enableHopWorlds ----
            enableHopWorlds.setText("Hop worlds");
            enableHopWorlds.setToolTipText("Hops worlds if the conditions are met");
            panel1.add(enableHopWorlds, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- powerChop ----
            powerChop.setText("Power chop");
            powerChop.setToolTipText("Drop all logs");
            panel1.add(powerChop, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- progressiveMode ----
            progressiveMode.setText("Progressive mode");
            progressiveMode.setToolTipText("Automatically switches over to the next tree type");
            panel1.add(progressiveMode, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

            //---- enableAntiban ----
            enableAntiban.setText("Enable anti-ban");
            enableAntiban.setToolTipText("Enables small tasks that try to replicate normal user behavior");
            panel1.add(enableAntiban, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));
        }
        contentPane.add(panel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        //======== hopSetup ========
        {
            hopSetup.setBorder(new TitledBorder("Hop Constraints"));
            hopSetup.setLayout(new GridBagLayout());
            ((GridBagLayout) hopSetup.getLayout()).columnWidths = new int[]{73, 51, 87, 45, 73, 0};
            ((GridBagLayout) hopSetup.getLayout()).rowHeights = new int[]{0, 0, 0, 0};
            ((GridBagLayout) hopSetup.getLayout()).columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) hopSetup.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 1.0E-4};

            //---- label3 ----
            label3.setText("Hop when");
            hopSetup.add(label3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 5), 0, 0));
            hopSetup.add(playerLim, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- label4 ----
            label4.setText("players every");
            hopSetup.add(label4, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 5), 0, 0));
            hopSetup.add(hopInterval, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- label5 ----
            label5.setText("minutes");
            hopSetup.add(label5, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 0), 0, 0));
        }
        contentPane.add(hopSetup, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
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

        //======== progressiveModeOptions ========
        {
            progressiveModeOptions.setBorder(new TitledBorder("Progressive Mode Options"));
            progressiveModeOptions.setLayout(new GridBagLayout());
            ((GridBagLayout) progressiveModeOptions.getLayout()).columnWidths = new int[]{0, 26, 0, 0};
            ((GridBagLayout) progressiveModeOptions.getLayout()).rowHeights = new int[]{0, 0, 0, 0};
            ((GridBagLayout) progressiveModeOptions.getLayout()).columnWeights = new double[]{0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) progressiveModeOptions.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 1.0E-4};

            //---- sellLogsAtGE ----
            sellLogsAtGE.setText("Sell logs to upgrade axe");
            sellLogsAtGE.setToolTipText("Sell logs at the Grand Exchange and buys axes up to adamant");
            progressiveModeOptions.add(sellLogsAtGE, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- skipWillowTrees ----
            skipWillowTrees.setText("Skip willow trees");
            skipWillowTrees.setToolTipText("Skip the willow trees to avoid the dark wizards");
            progressiveModeOptions.add(skipWillowTrees, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));
        }
        contentPane.add(progressiveModeOptions, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        //---- startScript ----
        startScript.setText("Start");
        contentPane.add(startScript, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        progressiveModeOptions.setVisible(false);

        progressiveMode.addActionListener(e -> {
            if (progressiveMode.isSelected()) {
                progressiveModeOptions.setVisible(true);
                Constants.enableProgressiveMode(true);
                pack();
            } else if (!progressiveMode.isSelected()) {
                progressiveModeOptions.setVisible(false);
                Constants.enableProgressiveMode(false);
                pack();
            }
        });

        sellLogsAtGE.addActionListener(e -> {
            if (sellLogsAtGE.isSelected()) {
                Constants.sellLogsAtGE(true);
            } else if (!sellLogsAtGE.isSelected())
                Constants.sellLogsAtGE(false);
        });

        skipWillowTrees.addActionListener(e -> {
            if (skipWillowTrees.isSelected()) {
                Constants.progressiveSkipWillows(true);
            } else if (!skipWillowTrees.isSelected())
                Constants.progressiveSkipWillows(false);
        });

        Constants.setSelectedTree(treeTypes.getItemAt(treeTypes.getSelectedIndex()));

        treeTypes.addActionListener(e -> {
            String selectedTree = treeTypes.getSelectedItem().toString();
            if (!selectedTree.equals(""))
                Constants.setSelectedTree(selectedTree);
        });

        enableAntiban.addActionListener(e -> {
            if (enableAntiban.isSelected()) {
                Constants.setEnableAntiBan(true);
            } else if (!enableAntiban.isSelected())
                Constants.setEnableAntiBan(false);
        });

        playerLim.addChangeListener(e -> {
            int hopPlayer = (Integer) playerLim.getValue();
            if (hopPlayer < 0)
                playerLim.setValue(0);
        });
        hopInterval.addChangeListener(e -> {
            int hopMins = (Integer) hopInterval.getValue();
            if (hopMins < 0)
                hopInterval.setValue(0);
        });

        powerChop.addActionListener(e -> {
            if (powerChop.isSelected()) {
                Constants.powerChopping(true);
            } else if (!powerChop.isSelected())
                Constants.powerChopping(false);
        });

        startScript.addActionListener(e -> {
            synchronized (lock) {
                lock.notify();
            }
            if (enableHopWorlds.isSelected())
                Constants.setHopValues((Integer) playerLim.getValue(), (Integer) hopInterval.getValue());
            setVisible(false);
        });


        //Setup default values
        treeTypes.setSelectedIndex(0);
        playerLim.setValue(3);
        hopInterval.setValue(5);
        Constants.powerChopping(false);
        Constants.setEnableAntiBan(true);
        Constants.sellLogsAtGE(false);
        Constants.enableProgressiveMode(false);
        Constants.progressiveSkipWillows(true);

        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }

}
