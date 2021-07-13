package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.*;
import com.techelevator.view.ConsoleService;
import io.cucumber.java.bs.A;

import java.util.List;
import java.util.Scanner;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService accountService;
    private TransferService transferService;
    private UserService userService;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		double result = accountService.getBalanceByUserId(currentUser.getUser().getId());
		System.out.printf("Your current account balance is: %.2f", result);
	}

	private void viewTransferHistory() {
		Transfer[] result = transferService.getTransfersListByUserId(currentUser.getUser().getId());
		String rowTemplate = "%s\t\t\t%s: %s\t\t\t$ %.2f";
		System.out.println("-------------------------------------------");
		System.out.println("Transfers");
		System.out.println("ID\t\t\tFrom/To\t\t\tAmount");
		System.out.println("-------------------------------------------");
		for(Transfer t : result) {
			String name;
			String fromTo;
			if(t.getToUsername().equals(currentUser.getUser().getUsername())) {
				name = t.getFromUsername();
				fromTo = "From";
			} else
			{
				name = t.getToUsername();
				fromTo = "To";
			}
			System.out.println(String.format(rowTemplate, t.getTransferId(), fromTo, name, t.getAmount()));
		}

		System.out.println("Please enter transfer ID to view details (0 to cancel):");

		Scanner scanner = new Scanner(System.in);
		String line = scanner.nextLine();
		while (!line.equals("0")) {
			try {
				int transferId = Integer.parseInt(line);
				Transfer transferFound =  null;
				for(Transfer t : result) {
					if(t.getTransferId() == transferId) {
						transferFound = t;
						break;
					}
				}

				if(transferFound != null) {
					System.out.println("-------------------------------------------");
					System.out.println("Transfer Details");
					System.out.println("-------------------------------------------");
					System.out.println("Id: " + transferFound.getTransferId());
					System.out.println("From: " + transferFound.getFromUsername());
					System.out.println("To: " + transferFound.getToUsername());
					System.out.println("Type: " + transferFound.getTransferType());
					System.out.println("Status: " + transferFound.getTransferStatus() );
					System.out.println("Amount: " + transferFound.getAmount() );

					break;
				}

				System.out.println("Transfer not found, please try again!");
				System.out.println("Please enter transfer ID to view details (0 to cancel):");
				line = scanner.nextLine();
			} catch(NumberFormatException e) {
				System.out.println("Invalid transfer ID.");
				System.out.println("Please enter transfer ID to view details (0 to cancel):");
				line = scanner.nextLine();
			}
		}
	}

	//Optional:
	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		List<User> users = userService.getAllUsers();



		System.out.println("-------------------------------------------\n" +
				"Users\n" +
				"ID Name\n" +
				"-------------------------------------------");
		for(User u : users) {
			System.out.println(u.getId() + " " + u.getUsername());
		}
		System.out.println("----------");


		System.out.println("Enter ID of user you are sending to (0 to cancel):");

		Scanner scanner = new Scanner(System.in);
		String line = scanner.nextLine();
		while (!line.equals("0")) {
			try {
				int userId = Integer.parseInt(line);
				User userFound =  null;
				for(User u : users) {
					if(u.getId() == userId) {
						userFound = u;
						break;
					}
				}

				if(userFound != null) {
					while(true) {
						System.out.println("Enter amount:");
						line = scanner.nextLine();
						try {
							double amount = Double.parseDouble(line);
							double balance = accountService.getBalanceByUserId(currentUser.getUser().getId());
							if(amount > balance) {
								System.out.println("Not enough funds!");
								return;
							}
							transferService.addTransfer(userFound.getId(), amount);
							return;
						} catch (NumberFormatException e) {
							System.out.println("Please enter a number!");
						}
					}

				}

				System.out.println("User not found, please try again!");
				System.out.println("Enter ID of user you are sending to (0 to cancel):");
				line = scanner.nextLine();
			} catch(NumberFormatException e) {
				System.out.println("Invalid User ID.");
				System.out.println("Enter ID of user you are sending to (0 to cancel):");
				line = scanner.nextLine();
			}
		}
	}

	//Optional:
	private void requestBucks() {
		// TODO Auto-generated method stub

	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
				transferService = new TransferService(API_BASE_URL, currentUser);
				accountService = new AccountService(API_BASE_URL, currentUser);
				userService = new UserService(API_BASE_URL, currentUser);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
