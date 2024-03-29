package ru.geekbrains.lesson5;

import java.util.*;

public class Program {

    static Scanner scanner = new Scanner(System.in);

    static String menu = """
            *** МОЙ 3D РЕДАКТОР ***
            =======================
            1. Открыть проект
            2. Сохранить проект
            3. Отобразить параметры проекта
            4. Отобразить все модели проекта
            5. Отобразить все текстуры проекта
            6. Выполнить рендер всех моделей
            7. Выполнить рендер модели
            8. Выполнить удаление модели
            9. Выполнить удаление текстуры
            0. ЗАВЕРШЕНИЕ РАБОТЫ ПРИЛОЖЕНИЯ
            Пожалуйста, выберите пункт меню:\s""";


    /**
     * Необходимо разделить на горизонтальные уровни "Редактор 3D графики".
     * Один пользователь. Программа работает на одном компьютере без выхода в сеть.
     *
     * Что видит пользователь, как взаимодействует? (Панель загрузки, блок редактирования, блок просмотра).
     * Какие задачи можно делать – функции системы? (Загрузить 3D модель, рассмотреть 3D модель, создать новую,
     * редактировать вершины, текстуры, сделать рендер, сохранить рендер).
     * Какие и где хранятся данные? (файлы 3D моделей, рендеры, анимация .., в файловой системе компьютера).
     *
     * Предложить варианты связывания всех уровней – сценарии использования. 3-4 сценария.
     * Сквозная функция – создать новую 3D модель, сделать рендер для печати на принтере…
     */
    public static void main(String[] args) {
        Editor3D editor3D = new Editor3D();
        while (true){
            System.out.print(menu);
            if (scanner.hasNextInt()){
                int no = scanner.nextInt();
                scanner.nextLine();
                try {
                    switch (no) {
                        case 0 -> {
                            System.out.println("Завершение работы приложения");
                            return;
                        }
                        case 1 -> {
                            System.out.print("Укажите наименование файла проекта: ");
                            String fileName = scanner.nextLine();
                            editor3D.openProject(fileName);
                            System.out.println("Проект успешно открыт.");
                        }
                        case 2 -> {
                            editor3D.saveProject();
                        }
                        case 3 -> {
                            editor3D.showProjectSettings();
                        }
                        case 4 -> {
                            editor3D.printAllModels();
                        }
                        case 5 -> {
                            editor3D.printAllTextures();
                        }
                        case 6 -> {
                            editor3D.renderAll();
                        }
                        case 7 -> {
                            editor3D.renderModel(entryNumber("модели"));
                        }
                        case 8 -> {
                            editor3D.deleteModel(entryNumber("модели"));
                        }
                        case 9 -> {
                            editor3D.deleteTexture(entryNumber("текстуры"));
                        }
                        default ->
                            System.out.println("Укажите корректный пункт меню.");
                    }
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
            else {
                System.out.println("Укажите корректный пункт меню.");
                scanner.nextLine();
            }
        }
    }

    private static int entryNumber(String text) {
        System.out.printf("Укажите номер %s: ", text);
        if (scanner.hasNextInt()) {
            int textureNo = scanner.nextInt();
            scanner.nextLine();
            return textureNo;
        }
        System.out.printf("Номер %s указан некорректно.",text);
        return -1;
    }
}

/**
 * UI (User Interface)
 */
class Editor3D implements UILayer{

    private ProjectFile projectFile;
    private BusinessLogicalLayer businessLogicalLayer;

    private DatabaseAccess databaseAccess;

    private Database database;


    private void initialize(){
        database = new EditorDatabase(projectFile);
        databaseAccess = new EditorDatabaseAccess(database);
        businessLogicalLayer = new EditorBusinessLogicalLayer(databaseAccess);
    }

    @Override
    public void openProject(String fileName) {
        this.projectFile = new ProjectFile(fileName);
        initialize();
    }

    @Override
    public void showProjectSettings() {
        // Предусловие
        checkProjectFile();

        System.out.println("*** Project v1 ***");
        System.out.println("******************");
        System.out.printf("fileName: %s\n", projectFile.getFileName());
        System.out.printf("setting1: %d\n", projectFile.getSetting1());
        System.out.printf("setting2: %s\n", projectFile.getSetting2());
        System.out.printf("setting3: %s\n", projectFile.getSetting3());
        System.out.println("******************");
    }

    private void checkProjectFile(){
        if (projectFile == null)
            throw new RuntimeException("Файл проекта не определен.");
    }

    @Override
    public void saveProject() {
        // Предусловие
        checkProjectFile();

        database.save();
        System.out.println("Изменения успешно сохранены.");
    }

    @Override
    public void printAllModels() {
        // Предусловие
        checkProjectFile();

        ArrayList<Model3D> models = (ArrayList<Model3D>)businessLogicalLayer.getAllModels();
        for (int i = 0; i < models.size(); i++){
            System.out.printf("===%d===\n", i);
            System.out.println(models.get(i));
            for (Texture texture: models.get(i).getTextures()) {
                System.out.printf("\t%s\n", texture);
            }
        }

    }

    @Override
    public void printAllTextures() {
        // Предусловие
        checkProjectFile();

        ArrayList<Texture> textures = (ArrayList<Texture>)businessLogicalLayer.getAllTextures();
        for (int i = 0; i < textures.size(); i++){
            System.out.printf("===%d===\n", i);
            System.out.println(textures.get(i));
        }
    }

    @Override
    public void renderAll() {
        // Предусловие
        checkProjectFile();


        System.out.println("Подождите ...");
        long startTime = System.currentTimeMillis();
        businessLogicalLayer.renderAllModels();
        long endTime = (System.currentTimeMillis() - startTime);
        System.out.printf("Операция выполнена за %d мс.\n", endTime);
    }

    @Override
    public void renderModel(int i) {
        // Предусловие
        checkProjectFile();

        ArrayList<Model3D> models = (ArrayList<Model3D>)businessLogicalLayer.getAllModels();
        if (i < 0 || i > models.size() - 1)
            throw new RuntimeException("Номер модели указан некорректною.");
        System.out.println("Подождите ...");
        long startTime = System.currentTimeMillis();
        businessLogicalLayer.renderModel(models.get(i));
        long endTime = (System.currentTimeMillis() - startTime);
        System.out.printf("Операция выполнена за %d мс.\n", endTime);
    }

    @Override
    public void deleteModel(int i) {
        // Предусловие
        checkProjectFile();

        ArrayList<Model3D> models = (ArrayList<Model3D>)businessLogicalLayer.getAllModels();
        if (i < 0 || i > models.size() - 1)
            throw new RuntimeException("Номер модели указан некорректною.");
        businessLogicalLayer.delete(models.get(i));
        System.out.println("Модель удалена.");
    }

    @Override
    public void deleteTexture(int i) {
        // Предусловие
        checkProjectFile();

        ArrayList<Texture> texture = (ArrayList<Texture>)businessLogicalLayer.getAllTextures();
        if (i < 0 || i > texture.size() - 1)
            throw new RuntimeException("Номер текстуры указан некорректно.");
        businessLogicalLayer.delete(texture.get(i));
        System.out.println("Текстура удалена.");
    }
}

/**
 * Интерфейс UI
 */
interface UILayer{

    void openProject(String fileName);
    void showProjectSettings();
    void saveProject();
    void printAllModels();
    void printAllTextures();
    void renderAll();
    void renderModel(int i);
    void deleteModel(int i);
    void deleteTexture(int i);
}

/**
 * Реализация Business Logical Layer
 */
class EditorBusinessLogicalLayer implements BusinessLogicalLayer{

    private DatabaseAccess databaseAccess;


    public EditorBusinessLogicalLayer(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    @Override
    public Collection<Model3D> getAllModels() {
        return databaseAccess.getAllModels();
    }

    @Override
    public Collection<Texture> getAllTextures() {
        return databaseAccess.getAllTextures();
    }

    @Override
    public void renderModel(Model3D model) {
        processRender(model);
    }

    @Override
    public void renderAllModels() {
        for (Model3D model : getAllModels())
            processRender(model);
    }

    @Override
    public void delete(Entity entity) {
        databaseAccess.removeEntity(entity);
        if (entity instanceof Texture) {
            for (Model3D model : databaseAccess.getAllModels()) {
                model.getTextures().remove(entity);
            }
        }
    }

    private Random random = new Random();

    private void processRender(Model3D model){
        try
        {
            Thread.sleep(2500 - random.nextInt(2000));
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

/**
 * Интерфейс BLL (Business Logical Layer)
 */
interface BusinessLogicalLayer{
    Collection<Model3D> getAllModels();

    Collection<Texture> getAllTextures();

    void renderModel(Model3D model);

    void renderAllModels();

    void delete(Entity entity);
}

/**
 * Реализация DAC
 */
class EditorDatabaseAccess implements DatabaseAccess{

    private final Database editorDatabase;

    public EditorDatabaseAccess(Database editorDatabase) {
        this.editorDatabase = editorDatabase;
    }

    @Override
    public Collection<Model3D> getAllModels() {
        Collection<Model3D> models = new ArrayList<>();
        for (Entity entity: editorDatabase.getAll()) {
            if (entity instanceof Model3D)
            {
                models.add((Model3D)entity);
            }
        }
        return models;
    }
    @Override
    public Collection<Texture> getAllTextures() {
        Collection<Texture> models = new ArrayList<>();
        for (Entity entity: editorDatabase.getAll()) {
            if (entity instanceof Texture)
            {
                models.add((Texture)entity);
            }
        }
        return models;
    }

    @Override
    public void addEntity(Entity entity) {
        editorDatabase.getAll().add(entity);
    }

    @Override
    public void removeEntity(Entity entity) {
        editorDatabase.getAll().remove(entity);
    }
}

/**
 * Интерфейс DAC
 */
interface DatabaseAccess{
    void addEntity(Entity entity);
    void removeEntity(Entity entity);
    Collection<Texture> getAllTextures();
    Collection<Model3D> getAllModels();
}

/**
 * Database
 */
class EditorDatabase implements Database{

    private Random random = new Random();
    private final  ProjectFile projectFile;
    private Collection<Entity> entities;

    public EditorDatabase(ProjectFile projectFile) {
        this.projectFile = projectFile;
        load();
    }

    @Override
    public void load() {
        //TODO: Загрузка всех сущностей проекта (модели, текстуры и т. д)
    }

    @Override
    public void save() {
        //TODO: Сохранение текущего состояния всех сущностей проекта
    }

    public Collection<Entity> getAll(){
        if (entities == null){
            entities = new ArrayList<>();
            int entCount = random.nextInt(5, 11);
            for (int i = 0; i < entCount; i++) {
                generateModelAndTextures();
            }
        }
        return entities;
    }

    private void generateModelAndTextures(){
        Model3D model3D = new Model3D();
        int txCount = random.nextInt(3);
        for (int i = 0; i < txCount; i++){
            Texture texture = new Texture();
            model3D.getTextures().add(texture);
            entities.add(texture);
        }
        entities.add(model3D);
    }

}

/**
 * Интерфейс БД
 */
interface Database{
    void load();

    void save();

    Collection<Entity> getAll();
}

/**
 * 3D модель
 */
class Model3D implements Entity{

    private static int counter = 10000;
    private int id;

    private Collection<Texture> textures = new ArrayList<>();

    @Override
    public int getId() {
        return id;
    }

    {
        id = ++counter;
    }

    public Model3D(){

    }

    public Model3D(Collection<Texture> textures) {
        this.textures = textures;
    }

    public Collection<Texture> getTextures() {
        return textures;
    }

    @Override
    public String toString() {
        return String.format("3DModel #%s", id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model3D model3D = (Model3D) o;
        return id == model3D.id && Objects.equals(textures, model3D.textures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, textures);
    }
}

/**
 * Текстура
 */
class Texture implements Entity{

    private static int counter = 50000;

    private int id;

    {
        id = ++counter;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Texture #%s", id);
    }
}

/**
 * Сущность
 */
interface Entity{

    int getId();
}

/**
 * Файл проекта
 */
class ProjectFile{

    private String fileName;
    private int setting1;
    private String setting2;
    private boolean setting3;

    public ProjectFile(String fileName) {

        this.fileName = fileName;
        //TODO: Загрузка настроек проекта из файла

        setting1 = 1;
        setting2 = "...";
        setting3 = false;
    }

    public String getFileName() {
        return fileName;
    }

    public int getSetting1() {
        return setting1;
    }

    public String getSetting2() {
        return setting2;
    }

    public boolean getSetting3() {
        return setting3;
    }
}

