package ru.vasichevda.sbertexjunior;

public class Article {

    /**
     * Поле - заголовок
     */
    private String title;
    /**
     * Поле - описание
     */
    private String description;
    /**
     * Поле - автор
     */
    private String author;
    /**
     * Поле - стоимоть (адаптация поля опубликовано)
     */
    private Integer cost;
    //
    /**
     * Поле - в корзине - да/нет
     */
    private Boolean inBasket = false;

    //
    //Getters
    //

    /**
     * Получает значение поля title
     */
    String getTitle() {
        return title;
    }

    /**
     * Получает значение поля description
     */
    String getDescription() {
        return !description.equals("null") ? description : "";
    }

    /**
     * Получает значение поля author
     */
    String getAuthor() {
        return !author.equals("null") ? author : "";
    }

    /**
     * Получает значение поля cost в числовом представлении
     */
    Integer getCost() {
        return cost;
    }

    /**
     * Получает значение поля cost в строковом представлении
     */
    String getCostStr() {
        return String.valueOf(cost).concat(" RUB");
    }

    /**
     * Получает значение поля inBasket
     */
    Boolean getInBasket() {
        return inBasket;
    }

    //
    //Setters
    //

    /**
     * Задает значение поля title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Задает значение поля description
     */
    void setDescription(String description) {
        this.description = description;
    }

    /**
     * Задает значение поля author
     */
    void setAuthor(String author) {
        this.author = author;
    }

//    public void setCost(Integer cost) {
//        this.cost = cost;
//    }

    /**
     * Задает значение поля cost - адаптация использвования параметра publishedAt
     */
    void setCost(String publishedAt) {
        char[] publishedAtToCost = publishedAt.toCharArray();
        int totalCost = 0;
        for (char cost : publishedAtToCost) {
            totalCost += (int) cost;
        }
        this.cost = totalCost;
    }

    /**
     * Устанавливает значение поля inBasket
     */
    void setInBasket(Boolean inBasket) {
        this.inBasket = inBasket;
    }

}