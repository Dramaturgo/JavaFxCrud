package com.example.ejercicio1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloController implements Initializable {

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


    ObservableList<Books> booksList = FXCollections.observableArrayList();


    @FXML
    private void insertButton() {
        String query = "insert into books (Title,Author,Year,Pages)"+"values('"+ titleField.getText()+"','"+authorField.getText()+"','"+yearField.getText()+"','"+ pagesField.getText()+"')";
        executeQuery(query);
        clean ();
        showBooks ();

    }

    @FXML
    public void updateButton () {
        String query = "UPDATE books SET Title='"+titleField.getText()+"',Author='"+authorField.getText()+"',Year="+yearField.getText()+",Pages="+pagesField.getText()+" WHERE ID="+idField.getText()+"";
        executeQuery(query);
        showBooks();
    }
    @FXML
    public void deleteButton () {
        String query = "DELETE FROM books WHERE ID="+idField.getText()+"";
        executeQuery(query);
        showBooks();
        clean ();
    }

    public void clean() {
        idField.setText ("");
        titleField.setText ("");
        authorField.setText ("");
        yearField.setText ("");
        pagesField.setText ("");

    }
    public void selection(){

        Books b1=TableView.getItems ().get (TableView.getSelectionModel ().getSelectedIndex ());

        idField.setText (String.valueOf (b1.getId ()));
        titleField.setText (b1.getTitle ());
        authorField.setText (b1.getAuthor ());
        yearField.setText (String.valueOf (b1.getYear ()));
        pagesField.setText (String.valueOf (b1.getPages ()));
    }

    public void executeQuery(String query) {
        Connection conn = getConnection();
        Statement st;
        try {
            st = conn.createStatement();
            st.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bdbooks","root","");
            return conn;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void showBooks() {
        ObservableList<Books> list = getBooksList();

        idColumn.setCellValueFactory(new PropertyValueFactory<> ("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        pagesColumn.setCellValueFactory(new PropertyValueFactory<>("pages"));

        TableView.setItems(list);
    }

    public ObservableList<Books> getBooksList(){
        ObservableList<Books> booksList = FXCollections.observableArrayList();
        Connection connection = getConnection();
        String query = "SELECT ID,Title,Author,Year,Pages FROM books";
        Statement st;
        ResultSet rs;

        try {
            st = connection.createStatement();
            rs = st.executeQuery(query);
            Books books;
            while(rs.next()) {
                books = new Books(rs.getInt("Id"),rs.getString("Title"),rs.getString("Author"),rs.getInt("Year"),rs.getInt("Pages"));
                booksList.add(books);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return booksList;
    }


    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {

            showBooks ();
    }
}