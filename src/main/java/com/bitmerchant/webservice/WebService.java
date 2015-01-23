package com.bitmerchant.webservice;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.externalStaticFileLocation;
import static spark.SparkBase.setPort;
import static spark.SparkBase.staticFileLocation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletResponse;

import org.bitcoin.protocols.payments.Protos.Payment;
import org.bitcoin.protocols.payments.Protos.PaymentACK;
import org.bitcoin.protocols.payments.Protos.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.SparkBase;

import com.bitmerchant.db.Actions;
import com.bitmerchant.db.Actions.OrderActions;
import com.bitmerchant.db.Actions.PaymentActions;
import com.bitmerchant.db.Tables.Button;
import com.bitmerchant.db.Tables.Order;
import com.bitmerchant.db.Tables.OrderView;
import com.bitmerchant.tools.DataSources;
import com.bitmerchant.tools.Tools;
import com.bitmerchant.wallet.LocalWallet;


/*
 * TODO not all of these should be exposed to the web. Really only the paymentrequest
 * 
 */

public class WebService {


	static final Logger log = LoggerFactory.getLogger(WebService.class);






	public static void start() {
		setPort(DataSources.SPARK_WEB_PORT) ;

		staticFileLocation("/web"); // Static files
		//		staticFileLocation("/web/html"); // Static files
		//		externalStaticFileLocation(DataSources.CODE_DIR+ "/web");

		// Set up the secure keystore





		WalletService.setup();
		API.setup();



		get("/hello", (req, res) -> {
			Tools.allowOnlyLocalHeaders(req, res);
			return "hi from the bitmerchant wallet web service";
		});




	}

	public static void setupSSL() {

		try {
			if (new File(DataSources.KEYSTORE_FILE).exists() && 
					new File(DataSources.KEYSTORE_PASSWORD_FILE).exists()) {
				String pass;

				pass = new String(Files.readAllBytes(Paths.get(DataSources.KEYSTORE_PASSWORD_FILE)));

				SparkBase.setSecure(DataSources.KEYSTORE_FILE, pass,null,null);
				LocalWallet.INSTANCE.controller.setIsSSLEncrypted(true);
			} else {
				log.info("No Java Keystore Exists");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}


}