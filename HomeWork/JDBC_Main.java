
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

	static Connection conn;

	public static void main(String[] args) throws SQLException {
		String url = "jdbc:mysql://localhost:3306/mans?autoReconnect=true&useSSL=false";
		String username = "root";
		String password = "123456";
		Scanner sc = new Scanner(System.in);
		List<String> mansList = new ArrayList<>();

		conn = DriverManager.getConnection(url, username, password);
		System.out.println("Connected " + !conn.isClosed());

		createTable();
		boolean isRun = true;
		while (isRun) {
			System.out.println("1 - Додати людину до БД");
			System.out.println("2 - Видалити людину з БД по id.");
			System.out.println("3 - Показати список всіх людей в БД.");
			System.out.println("4 - Показати інформацію про одну людину по id.");
			System.out.println("5 - Наповнення таблиць рандомними даними.");
			System.out.println("6 - Вихід з програми.");
			switch (sc.next()) {
			case "1": {
				System.out.println("Введіть Імя:");
				String first_name = sc.next();
				System.out.println("Введіть Прізвище:");
				String last_name = sc.next();
				System.out.println("Введіть Вік:");
				int age = sc.nextInt();
				System.out.println("Введіть Хоббі:");
				String hobby = sc.next();
				addMan(first_name, last_name, age, hobby);
				break;
			}
			case "2": {
				deleteMans();
				break;
			}
			case "3": {
				printMans();
				break;
			}
			case "4": {
				System.out.println("Введіть ІD людини:");
				printManForID(sc.nextInt());
			}
			case "5": {
				addManRandom();
				break;
			}
			case "6": {
				isRun = false;
				break;
			}
			default:
				System.out.println("Такого пункту в меню немає!");
				break;
			}
		}
		conn.close();
	}

	static void createTable() throws SQLException {
		String dropQuery = "DROP TABLE IF EXISTS man;";
		String query = "CREATE TABLE man(" + "id INT PRIMARY KEY AUTO_INCREMENT, " + "first_name VARCHAR(100),"
				+ "last_name VARCHAR(100)," + "age INT," + "hobby VARCHAR(100)" + ");";

		Statement statement = conn.createStatement();
		statement.execute(dropQuery);
		statement.execute(query);
		statement.close();
	}

	static void addMan(String first_name, String last_name, int age, String hobby) throws SQLException {
		String query = "INSERT INTO man(first_name, last_name, age, hobby) VALUES(?, ?, ?, ?)";

		PreparedStatement preparedStatement = conn.prepareStatement(query);
		preparedStatement.setString(1, first_name);
		preparedStatement.setString(2, last_name);
		preparedStatement.setInt(3, age);
		preparedStatement.setString(4, hobby);
		preparedStatement.executeUpdate();
		preparedStatement.close();
	}

	static void deleteMans() throws SQLException {
		String query = "SELECT * FROM man;";
		PreparedStatement preparedStatement = conn.prepareStatement(query);
		ResultSet rs = preparedStatement.executeQuery();

		List<String> mansList = new ArrayList<>();

		while (rs.next()) {
			mansList.add("ID: " + rs.getInt("id") + " | " + "First Name: " + rs.getString("first_name") + " | "
					+ "Last Name: " + rs.getString("last_name") + " | " + "Age:" + rs.getInt("age") + " | " + "Hobby: "
					+ rs.getString("hobby"));
		}

		mansList.forEach(m -> System.out.println(m));

		System.out.println("Введіть ІD людини яку видалити:");
		int numberMan;
		Scanner sc = new Scanner(System.in);
		do {
			while (!sc.hasNextInt()) {
				System.out.println("Ви ввели не числове значення! Введіть ІD людини:");
				sc.next();
			}
			numberMan = sc.nextInt();
			if (numberMan < 0 || numberMan >= mansList.size() + 1)
				System.out.println("Людини з таким ID неіснує! Введіть ІD людини:");
		} while (numberMan < 0 || numberMan >= mansList.size() + 1);

		String queryDel = "delete from man where ID = ?;";
		preparedStatement = conn.prepareStatement(queryDel);
		preparedStatement.setInt(1, numberMan);
		preparedStatement.executeUpdate();
		preparedStatement.close();
	}

	static void printMans() throws SQLException {
		String query = "SELECT * FROM man;";
		PreparedStatement preparedStatement = conn.prepareStatement(query);
		ResultSet rs = preparedStatement.executeQuery();
		List<String> mansList = new ArrayList<>();
		while (rs.next()) {
			mansList.add("ID: " + rs.getInt("id") + " | " + "First Name: " + rs.getString("first_name") + " | "
					+ "Last Name: " + rs.getString("last_name") + " | " + "Age:" + rs.getInt("age") + " | " + "Hobby: "
					+ rs.getString("hobby"));
		}
		mansList.forEach(m -> System.out.println(m));
	}

	static void printManForID(int id) throws SQLException {
		String query = "SELECT * FROM man where ID = ?;";
		PreparedStatement preparedStatement = conn.prepareStatement(query);

		preparedStatement.setInt(1, id);
		ResultSet rs = preparedStatement.executeQuery();
		while (rs.next()) {
			System.out.println("ID: " + rs.getInt("id") + " | " + "First Name: " + rs.getString("first_name") + " | "
					+ "Last Name: " + rs.getString("last_name") + " | " + "Age:" + rs.getInt("age") + " | " + "Hobby: "
					+ rs.getString("hobby"));
		}
		preparedStatement.close();
	}

	static void addManRandom() throws SQLException {
		String[] first_name = { "Ivan", "Oleg", "Bohdan", "Igor", "Vova", "Andriy", "Jhonny", "Billy" };
		String[] last_name = { "Kolos", "Popov", "Hyk", "", "Vova", "Lytvyn", "Poroshenko", "Lytsenko" };
		String[] hobby = { "sport", "music", "java", "films", "football" };

		for (int i = 0; i < 10; i++) {

			String query = "INSERT INTO man(first_name, last_name, age, hobby) VALUES(?, ?, ?, ?)";

			Random rand = new Random();
			int r1 = rand.nextInt(first_name.length);
			int r2 = rand.nextInt(last_name.length);
			int r3 = rand.nextInt(40) + 18;
			int r4 = rand.nextInt(hobby.length);

			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, first_name[r1]);
			preparedStatement.setString(2, last_name[r2]);
			preparedStatement.setInt(3, r3);
			preparedStatement.setString(4, hobby[r4]);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		}
	}
}
