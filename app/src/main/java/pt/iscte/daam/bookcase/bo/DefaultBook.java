package pt.iscte.daam.bookcase.bo;

import java.util.List;

/**
 * Created by joaocarias on 23/03/16.
 */
public abstract class DefaultBook implements Book {

    protected String title;
    protected List<String> authors;
    protected String releaseYear;
    protected String releaseMonth;
    protected String releaseDay;

    //TODO: Colocar aqui os restantes atributos gerais dos livros

    // NOTA: As propriedades adicionais que sejam necessárias, ie., não comuns a todos os tipos de livro irão para as implementações epecificas de cada classe.
}
