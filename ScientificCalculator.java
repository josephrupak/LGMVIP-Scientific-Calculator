import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScientificCalculator extends JFrame {
    private JTextField displayField;
    private String expression = "";

    public ScientificCalculator() {
        setTitle("Scientific Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLayout(new BorderLayout());

        displayField = new JTextField();
        displayField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        displayField.setEditable(false);
        add(displayField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 4));

        String[] buttonLabels = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "sin", "cos", "tan", "sqrt",
            "(", ")", "←", "C"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            String buttonText = button.getText();

            switch (buttonText) {
                case "=":
                    evaluateExpression();
                    break;
                case "sin":
                    appendText("sin(");
                    break;
                case "cos":
                    appendText("cos(");
                    break;
                case "tan":
                    appendText("tan(");
                    break;
                case "sqrt":
                    appendText("sqrt(");
                    break;
                case "(":
                    appendText("(");
                    break;
                case ")":
                    appendText(")");
                    break;
                case "←":
                    backspace();
                    break;
                case "C":
                    clearAll();
                    break;
                default:
                    appendText(buttonText);
                    break;
            }
        }
    }

    private void appendText(String text) {
        expression += text;
        displayField.setText(expression);
    }

    private void backspace() {
        if (!expression.isEmpty()) {
            expression = expression.substring(0, expression.length() - 1);
            displayField.setText(expression);
        }
    }

    private void clearAll() {
        expression = "";
        displayField.setText("");
    }

    private void evaluateExpression() {
        try {
            double result = evaluate(expression);
            displayField.setText(String.valueOf(result));
            expression = "";
        } catch (ArithmeticException ex) {
            displayField.setText("Error: " + ex.getMessage());
        }
    }

    private double evaluate(String expression) {
        // Add your evaluation logic here
        // This example only supports basic arithmetic operations
        return new Object() {
            int index = -1;
            int ch;

            void nextChar() {
                ch = (++index < expression.length()) ? expression.charAt(index) : -1;
            }

            boolean isDigitChar() {
                return ch >= '0' && ch <= '9';
            }

            double parseNumber() {
                StringBuilder sb = new StringBuilder();
                while (isDigitChar()) {
                    sb.append((char) ch);
                    nextChar();
                }
                return Double.parseDouble(sb.toString());
            }

            double evaluateExpression() {
                nextChar();
                double value = evaluateAddSubtract();
                if (index < expression.length()) {
                    throw new ArithmeticException("Unexpected character: " + (char) ch);
                }
                return value;
            }

            double evaluateAddSubtract() {
                double value = evaluateMultiplyDivide();
                while (true) {
                    if (ch == '+') {
                        nextChar();
                        value += evaluateMultiplyDivide();
                    } else if (ch == '-') {
                        nextChar();
                        value -= evaluateMultiplyDivide();
                    } else {
                        return value;
                    }
                }
            }

            double evaluateMultiplyDivide() {
                double value = evaluateUnary();
                while (true) {
                    if (ch == '*') {
                        nextChar();
                        value *= evaluateUnary();
                    } else if (ch == '/') {
                        nextChar();
                        double divisor = evaluateUnary();
                        if (divisor == 0) {
                            throw new ArithmeticException("Division by zero");
                        }
                        value /= divisor;
                    } else {
                        return value;
                    }
                }
            }

            double evaluateUnary() {
                if (ch == '-') {
                    nextChar();
                    return -evaluateUnary();
                } else if (ch == '(') {
                    nextChar();
                    double value = evaluateAddSubtract();
                    if (ch != ')') {
                        throw new ArithmeticException("Missing closing parenthesis");
                    }
                    nextChar();
                    return value;
                } else if (ch == 's' || ch == 'c' || ch == 't') {
                    StringBuilder func = new StringBuilder();
                    while (Character.isLetter(ch)) {
                        func.append((char) ch);
                        nextChar();
                    }
                    if (ch != '(') {
                        throw new ArithmeticException("Missing opening parenthesis after function");
                    }
                    nextChar();
                    double value = evaluateAddSubtract();
                    if (ch != ')') {
                        throw new ArithmeticException("Missing closing parenthesis after function argument");
                    }
                    nextChar();
                    switch (func.toString()) {
                        case "sin":
                            return Math.sin(value);
                        case "cos":
                            return Math.cos(value);
                        case "tan":
                            return Math.tan(value);
                        case "sqrt":
                            return Math.sqrt(value);
                        default:
                            throw new ArithmeticException("Unknown function: " + func);
                    }
                } else if (isDigitChar()) {
                    return parseNumber();
                } else {
                    throw new ArithmeticException("Unexpected character: " + (char) ch);
                }
            }
        }.evaluateExpression();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        ScientificCalculator calculator = new ScientificCalculator();
        calculator.setVisible(true);
    }
}
