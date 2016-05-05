package pt.iscte.daam.bookcase.bo;

import java.util.List;

/**
 * Created by joaocarias on 23/03/16.
 */
public abstract class DefaultBook implements Book {

    protected String title;
    protected String authors;
    protected String releaseYear;
    protected String releaseMonth;
    protected String releaseDay;
    protected String codeISBN;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setReleaseMonth(String releaseMonth) {
        this.releaseMonth = releaseMonth;
    }

    public void setReleaseDay(String releaseDay) {
        this.releaseDay = releaseDay;
    }

    public void setCodeISBN(String codeISBN) {
        this.codeISBN = codeISBN;
    }

    //TODO: Colocar aqui os restantes atributos gerais dos livros

    // NOTE: As propriedades adicionais que sejam necessárias, ie., não comuns a todos os tipos de livro irão para as implementações epecificas de cada classe.
}
