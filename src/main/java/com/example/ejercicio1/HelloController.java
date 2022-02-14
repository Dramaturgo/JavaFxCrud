package com.example.ejercicio1;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;


import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import java.awt.Desktop;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.itextpdf.kernel.pdf.PdfName.*;


public class HelloController implements Initializable {

    @FXML
    public TextField searchField;

    @FXML
    public Button cancelButton;

    @FXML
    public Button generateButton;

    @FXML
    private TextField idField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private TextField yearField;

    @FXML
    private TextField pagesField;

    @FXML
    private Button insertButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableView<Books> TableView;

    @FXML
    private TableColumn <Books,Integer> idColumn;

    @FXML
    private TableColumn<Books, String> titleColumn;

    @FXML
    private TableColumn<Books, String> authorColumn;

    @FXML
    private TableColumn<Books, Integer> yearColumn;

    @FXML
    private TableColumn<Books, Integer> pagesColumn;



    public void generatePdf(){


        try {
            File file = new File("Reporte.pdf");
            PdfWriter pdfWriter = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);
            Paragraph paragraph = new Paragraph ();
            Style normal=new Style ();
            PdfFont font =PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            normal.setFont (font).setFontSize (20);
            paragraph.add (new Text ("Reporte de libros").addStyle (normal));
            paragraph.setTextAlignment (TextAlignment.CENTER);
            paragraph.add (new Text ("\n\n"));

            document.add(paragraph);

            Table table=new Table (5);
            table.setWidth(UnitValue.createPercentValue (100));
            Cell c1=new Cell ();
            Cell c2=new Cell ();
            Cell c3=new Cell ();
            Cell c4=new Cell ();
            Cell c5=new Cell ();
            c1.add (new Paragraph ("Id"));
            c2.add (new Paragraph ("Title"));
            c3.add (new Paragraph ("Author"));
            c4.add (new Paragraph ("Year"));
            c5.add (new Paragraph ("Pages"));
            c1.setTextAlignment (TextAlignment.CENTER);
            c2.setTextAlignment (TextAlignment.CENTER);
            c3.setTextAlignment (TextAlignment.CENTER);
            c4.setTextAlignment (TextAlignment.CENTER);
            c5.setTextAlignment (TextAlignment.CENTER);
            c1.setBackgroundColor (new DeviceRgb (40,150,202));
            c2.setBackgroundColor (new DeviceRgb (40,150,202));
            c3.setBackgroundColor (new DeviceRgb (40,150,202));
            c4.setBackgroundColor (new DeviceRgb (40,150,202));
            c5.setBackgroundColor (new DeviceRgb (40,150,202));
            table.addCell (c1);
            table.addCell (c2);
            table.addCell (c3);
            table.addCell (c4);
            table.addCell (c5);

            for (Books b:getBooksList ()){
            table.addCell (""+b.getId ());
            table.addCell (b.getTitle ());
            table.addCell (b.getAuthor ());
            table.addCell (""+b.getYear ());
            table.addCell (""+b.getPages ());

            }

            document.add (table);

            document.close();

            pdfWriter.close();

            Desktop.getDesktop().open (file);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void search () {
        ObservableList<Books> list = getBooksList ();
        FilteredList<Books> filteredData = new FilteredList<> (list, b -> true);
        searchField.textProperty ().addListener ((observableValue, oldValue, newValue) -> {
            filteredData.setPredicate (Books -> {
                if (newValue.isEmpty () || newValue.isBlank ()) {
                    return true;
                }
                String searchKeyword = newValue.toLowerCase ();

                if (Books.getTitle ().toLowerCase ().contains (searchKeyword)) {
                    return true;
                } else if (Books.getAuthor ().toLowerCase ().contains (searchKeyword)) {
                    return true;
                } else {
                    return false;
                }

            });
        });

        SortedList<Books> sortedData = new SortedList<> (filteredData);
        sortedData.comparatorProperty ().bind (TableView.comparatorProperty ());
        TableView.setItems (sortedData);
    }


    @FXML
    public void insertButton () {

        String query = "insert into books (Title,Author,Year,Pages)" + "values('" + titleField.getText () + "','" + authorField.getText () + "','" + yearField.getText () + "','" + pagesField.getText () + "')";
        executeQuery (query);
        clean ();
        showBooks ();
    }


    @FXML
    public void updateButton () {
        String query = "UPDATE books SET Title='" + titleField.getText () + "',Author='" + authorField.getText () + "',Year=" + yearField.getText () + ",Pages=" + pagesField.getText () + " WHERE ID=" + idField.getText () + "";
        executeQuery (query);
        showBooks ();
    }

    @FXML
    public void deleteButton () {
        String query = "DELETE FROM books WHERE ID=" + idField.getText () + "";
        executeQuery (query);
        showBooks ();
        clean ();
    }

    public void clean () {
        idField.setText ("");
        titleField.setText ("");
        authorField.setText ("");
        yearField.setText ("");
        pagesField.setText ("");

    }

    public void selection () {

        Books b1 = TableView.getItems ().get (TableView.getSelectionModel ().getSelectedIndex ());

        idField.setText (String.valueOf (b1.getId ()));
        titleField.setText (b1.getTitle ());
        authorField.setText (b1.getAuthor ());
        yearField.setText (String.valueOf (b1.getYear ()));
        pagesField.setText (String.valueOf (b1.getPages ()));
    }

    public void executeQuery (String query) {
        Connection conn = getConnection ();
        Statement st;
        try {
            st = conn.createStatement ();
            st.executeUpdate (query);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    public Connection getConnection () {
        Connection conn;
        try {
            conn = DriverManager.getConnection ("jdbc:mysql://localhost:3306/bdbooks", "root", "");
            return conn;
        } catch (Exception e) {
            e.printStackTrace ();
            return null;
        }
    }

    public void showBooks () {

        ObservableList<Books> list = getBooksList ();

        idColumn.setCellValueFactory (new PropertyValueFactory<> ("id"));
        titleColumn.setCellValueFactory (new PropertyValueFactory<> ("title"));
        authorColumn.setCellValueFactory (new PropertyValueFactory<> ("author"));
        yearColumn.setCellValueFactory (new PropertyValueFactory<> ("year"));
        pagesColumn.setCellValueFactory (new PropertyValueFactory<> ("pages"));

        TableView.setItems (list);
        search ();

    }

    public ObservableList<Books> getBooksList () {

        ObservableList<Books> booksList = FXCollections.observableArrayList ();
        Connection connection = getConnection ();
        String query = "SELECT ID,Title,Author,Year,Pages FROM books";
        Statement st;
        ResultSet rs;

        try {
            st = connection.createStatement ();
            rs = st.executeQuery (query);
            Books books;
            while (rs.next ()) {
                books = new Books (rs.getInt ("Id"), rs.getString ("Title"), rs.getString ("Author"), rs.getInt ("Year"), rs.getInt ("Pages"));
                booksList.add (books);
            }

        } catch (Exception e) {
            e.printStackTrace ();
        }


        return booksList;

    }


    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {

        showBooks ();


    }
}