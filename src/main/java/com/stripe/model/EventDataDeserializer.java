package com.stripe.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.stripe.net.APIResource;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EventDataDeserializer implements JsonDeserializer<EventData> {

	@SuppressWarnings("rawtypes")
	static final Map<String, Class> objectMap = new HashMap<String, Class>();
	static {
		objectMap.put("balance", Balance.class);
		objectMap.put("balance_transaction", BalanceTransaction.class);
		objectMap.put("charge", Charge.class);
		objectMap.put("customer", Customer.class);
		objectMap.put("dispute", Dispute.class);
		objectMap.put("event", Event.class);
		objectMap.put("file_upload", FileUpload.class);
		objectMap.put("refund", Refund.class);
		objectMap.put("token", Token.class);
		objectMap.put("transfer", Transfer.class);
		objectMap.put("transfer_reversal", Reversal.class);
		objectMap.put("account", Account.class);
		objectMap.put("fee_refund", FeeRefund.class);
		objectMap.put("application_fee", ApplicationFee.class);
		objectMap.put("recipient", Recipient.class);
		objectMap.put("alipay_account", AlipayAccount.class);
		objectMap.put("bank_account", BankAccount.class);
		objectMap.put("bitcoin_receiver", BitcoinReceiver.class);
		objectMap.put("card", Card.class);
		objectMap.put("order", Order.class);
		objectMap.put("order_item", OrderItem.class);
		objectMap.put("order_return", OrderReturn.class);
		objectMap.put("product", Product.class);
		objectMap.put("sku", SKU.class);
		objectMap.put("coupon", Coupon.class);
		objectMap.put("discount", Discount.class);
		objectMap.put("invoice", Invoice.class);
		objectMap.put("invoice_line_item", InvoiceLineItem.class);
		objectMap.put("invoiceitem", InvoiceItem.class);
		objectMap.put("plan", Plan.class);
		objectMap.put("subscription", Subscription.class);
		objectMap.put("summary", Summary.class);
		objectMap.put("fee", Fee.class);
		objectMap.put("three_d_secure", ThreeDSecure.class);
		objectMap.put("apple_pay_domain", ApplePayDomain.class);
		objectMap.put("review", Review.class);
	}

	private Object deserializeJsonPrimitive(JsonPrimitive element) {
		if (element.isBoolean()) {
			return element.getAsBoolean();
		} else if (element.isNumber()) {
			return element.getAsNumber();
		} else {
			return element.getAsString();
		}
	}

	private Object[] deserializeJsonArray(JsonArray arr) {
		Object[] elems = new Object[arr.size()];
		Iterator<JsonElement> elemIter = arr.iterator();
		int i = 0;
		while (elemIter.hasNext()) {
			JsonElement elem = elemIter.next();
			elems[i++] = deserializeJsonElement(elem);
		}
		return elems;
	}

	private Object deserializeJsonElement(JsonElement element) {
		if (element.isJsonNull()) {
			return null;
		} else if (element.isJsonObject()) {
			Map<String, Object> valueMap = new HashMap<String, Object>();
			populateMapFromJSONObject(valueMap, element.getAsJsonObject());
			return valueMap;
		} else if (element.isJsonPrimitive()) {
			return deserializeJsonPrimitive(element.getAsJsonPrimitive());
		} else if (element.isJsonArray()) {
			return deserializeJsonArray(element.getAsJsonArray());
		} else {
			System.err.println("Unknown JSON element type for element " + element + ". " +
					"If you're seeing this messaage, it's probably a bug in the Stripe Java " +
					"library. Please contact us by email at support@stripe.com.");
			return null;
		}
	}

	private void populateMapFromJSONObject(Map<String, Object> objMap, JsonObject jsonObject) {
		for(Map.Entry<String, JsonElement> entry: jsonObject.entrySet()) {
			String key = entry.getKey();
			JsonElement element = entry.getValue();
			objMap.put(key, deserializeJsonElement(element));
		}
	}

	@SuppressWarnings("unchecked")
	public EventData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		EventData eventData = new EventData();
		JsonObject jsonObject = json.getAsJsonObject();
		for(Map.Entry<String, JsonElement> entry: jsonObject.entrySet()) {
			String key = entry.getKey();
			JsonElement element = entry.getValue();
			if("previous_attributes".equals(key)) {
				if (element.isJsonNull()) {
					eventData.setPreviousAttributes(null);
				} else if (element.isJsonObject()) {
					Map<String, Object> previousAttributes = new HashMap<String, Object>();
					populateMapFromJSONObject(previousAttributes, element.getAsJsonObject());
					eventData.setPreviousAttributes(previousAttributes);
				}
			} else if ("object".equals(key)) {
				String type = element.getAsJsonObject().get("object").getAsString();
				Class<StripeObject> cl = objectMap.get(type);
				StripeObject object = APIResource.GSON.fromJson(entry.getValue(), cl != null ? cl : StripeRawJsonObject.class);
				eventData.setObject(object);
			}
		}
		return eventData;
	}
}
