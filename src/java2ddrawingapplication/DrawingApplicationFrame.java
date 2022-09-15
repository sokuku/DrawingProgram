package java2ddrawingapplication;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.lang.Integer;

public class DrawingApplicationFrame extends JFrame{
        private JComboBox shape;
        private JButton color1;
        private JButton color2;
        private JButton undo;
        private JButton clear;
        private JCheckBox fill;
        private JCheckBox gradient;
        private JCheckBox dash;
        private JSpinner strokeWidth;
        private JSpinner strokeLen;
        
        private JLabel mouseLocation;
        private JLabel shapeLabel;
        private JLabel optionLabel;
        private JLabel widthLabel;
        private JLabel dashLabel;
        private JPanel shapeColor;
        private JPanel lineOptions;
        private Color c1;
        private Color c2;     
        private ArrayList<MyShapes> shapes;
        private GridBagConstraints gbc;

    
    public DrawingApplicationFrame(){
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        
        buildHeader();
        buildDrawPanel();
        buildMouseCoord();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Java 2D Shapes");
        setVisible(true);
    }
    
    private void buildHeader(){
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; // fill horizontally, no margins
        
        buildShapeColor();
        gbc.gridy = 0;
        add(shapeColor, gbc);
        
        buildLineOptions();
        gbc.gridy = 1;
        add(lineOptions, gbc);    
        
    }
    
    private void buildShapeColor(){
        shapeColor = new JPanel();
        shapeLabel = new JLabel("Shape: ");
        String shapeList[] = new String[]{"Line", "Oval", "Rectangle"};
        shape = new JComboBox(shapeList);
        
        color1 = new JButton("1st Color");
        color2 = new JButton("2nd Color");
        undo = new JButton("Undo");
        clear = new JButton("Clear");
        
        color1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                 c1 = JColorChooser.showDialog(color1, "Choose 1st Color", c1);  
            }
        });
        
        color2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                 c2 = JColorChooser.showDialog(color2, "Choose 2nd Color", c2);  
            }
        });
        
        undo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                shapes.remove(shapes.size()-1);
                repaint();
            }
        });
        
        clear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                shapes.clear();
                repaint();
            }
        });
        
        shapeColor.setBackground(Color.cyan);   
        shapeColor.add(shapeLabel);
        shapeColor.add(shape);
        shapeColor.add(color1);
        shapeColor.add(color2);
        shapeColor.add(undo);
        shapeColor.add(clear);
    }
    
    private void buildLineOptions(){
        fill = new JCheckBox("Filled");
        gradient = new JCheckBox("Use Gradient");
        dash = new JCheckBox("Dashed");
        
        SpinnerModel sm1 = new SpinnerNumberModel(1, 0, 99, 1); // minimum line width of 0
        SpinnerModel sm2 = new SpinnerNumberModel(1, 1, 99, 1); // minimum dash length of 1
        strokeWidth = new JSpinner(sm1);
        strokeLen = new JSpinner(sm2);
        
        mouseLocation = new JLabel();
        optionLabel = new JLabel("Options: ");
        widthLabel = new JLabel("Line Width: ");
        dashLabel = new JLabel("Dash Length: ");
        
        lineOptions = new JPanel();
        lineOptions.setBackground(Color.cyan);        
        lineOptions.add(optionLabel);
        lineOptions.add(fill);
        lineOptions.add(gradient);
        lineOptions.add(dash);
        lineOptions.add(widthLabel);
        lineOptions.add(strokeWidth);
        lineOptions.add(dashLabel);
        lineOptions.add(strokeLen);
    }
    
    private void buildDrawPanel(){
        DrawPanel drawPanel = new DrawPanel();
        gbc.gridy = 2;
        gbc.weighty = 0.7;
        add(drawPanel, gbc);        
    }
    
    private void buildMouseCoord(){
        gbc.gridy = 3;
        gbc.weighty = 0.01;
        add(mouseLocation, gbc);
    }
    
    private class DrawPanel extends JPanel{
        private Paint paint;
        private BasicStroke stroke;
        private Point origin;
        private Point end;
        private Graphics2D g2d;
        private MyShapes tempShape;
        
        public DrawPanel(){
            this.addMouseListener(new MouseHandler());
            this.addMouseMotionListener(new MouseHandler());
            
            //set default colors
            c1 = Color.RED;
            c2 = Color.RED;
            shapes = new ArrayList();
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g2d = (Graphics2D) g;
                      
            //loop through and draw each shape in the shapes ArrayList
            for(int i = 0; i < shapes.size(); i++){
                shapes.get(i).draw(g2d);
            }
            
            //draw shape preview
            if(tempShape != null){
                tempShape.draw(g2d);
            }
        }

        private class MouseHandler extends MouseAdapter implements MouseMotionListener{

            public void mousePressed(MouseEvent event){     
                origin = new Point(event.getX(), event.getY());
                
                if(gradient.isSelected()){
                    paint = new GradientPaint(0, 0, c1, 50, 50, c2, true);
                }
                else{
                    paint = c1;
                }
                
                if(dash.isSelected()){
                    float len = ((Integer)strokeLen.getValue()).floatValue();
                    float[] dashLen = new float[1];
                    dashLen[0] = len;
                    stroke = new BasicStroke(((Integer)strokeWidth.getValue()).floatValue(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashLen, 0);
                }
                else{
                    stroke = new BasicStroke(((Integer)strokeWidth.getValue()).floatValue(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                }
            }

            @Override
            public void mouseReleased(MouseEvent event){
                end = new Point(event.getX(), event.getY()); 
                String shapeType = shape.getSelectedItem().toString();
                
                if(shapeType.equals("Line")){
                    shapes.add(new MyLine(origin, end, paint, stroke));
                }
                else if(shapeType.equals("Oval")){
                    shapes.add(new MyOval(origin, end, paint, stroke, fill.isSelected()));
                }
                else if(shapeType.equals("Rectangle")){
                    shapes.add(new MyRectangle(origin, end, paint, stroke, fill.isSelected()));
                }
                
                tempShape = null;
                repaint();
            }            
            
            @Override
            public void mouseDragged(MouseEvent event){
                Point temp = new Point(event.getX(), event.getY());
                String shapeType = shape.getSelectedItem().toString();
                
                if(shapeType.equals("Line")){
                    tempShape = new MyLine(origin, temp, paint, stroke);
                }
                else if(shapeType.equals("Oval")){
                    tempShape = new MyOval(origin, temp, paint, stroke, fill.isSelected());
                }
                else if(shapeType.equals("Rectangle")){
                    tempShape = new MyRectangle(origin, temp, paint, stroke, fill.isSelected());
                }
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent event){
                mouseLocation.setText("("+event.getX()+", "+event.getY()+")");
            }
        }   
    }
}
