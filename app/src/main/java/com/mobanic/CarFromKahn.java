package com.mobanic;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.text.NumberFormat;
import java.util.Locale;

@ParseClassName("CarFromKahn")
public class CarFromKahn extends ParseObject {

    private String[] mPossibleMakes = new String[]{
            "Aston Martin", "BMW", "Bentley",
            "Bugatti", "Defender", "Discovery", "Ferrari",
            "Jaguar", "Jeep", "Lamborghini",
            "Range Rover", "Maybach", "Mercedes-Benz",
            "Porsche", "Rolls Royce"
    };

    public CarFromKahn() {
    }

    public CarFromKahn(String header, String year, String imageId, String price, String color, String mileage, String fuelAndTrans, String location, boolean isLeftHanded) {
        setTitleAndMake(header);
        setYear(year);
        setCoverImage(imageId);
        setPrice(price);
        setColor(color);
        setMileage(mileage);
        setFuelAndTrans(fuelAndTrans);
        setLocation(location);
        setLeftHanded(isLeftHanded);
    }

    public String getMake() {
        return getString("make");
    }

    public String getModel() {
        return getString("model");
    }

    public void setTitleAndMake(String header) {
        String make = null;
        String model = null;
        for (String possibleMake : mPossibleMakes) {
            if (header.contains(possibleMake)) {
                make = possibleMake;
                String[] parts = header.split(" - ");
                String fragmentToRemove = parts[parts.length - 1];
                model = header.substring(make.length()).replaceFirst(" ", "").replace(fragmentToRemove, "").replace("- ", "");
                if (model.contains("(")) {
                    fragmentToRemove = model.substring(model.indexOf("(") - 1, model.indexOf(")") + 1);
                    // TODO Go thought the other models and remove redundant prefixes from models
                    model = model.replace(fragmentToRemove, "").replace("(LHD)", "").replace(" (Turbo Wide Body)", "");
                }
            }
        }
        put("make", make);
        put("model", model);
    }

    public String getFormattedPrice() {
        if (getInt("price") == 0) {
            return "Under offer";
        } else if (getInt("price") == 1) {
            return "POA";
        } else {
            return formatPrice(getInt("price"));
        }
    }

    public void setPrice(String priceStr) {
        if (priceStr.contains(".00")) {
            int price = Integer.parseInt(
                    priceStr.replace(".00", "").replaceAll("\\D+", ""));
            put("price", price);
        } else if (priceStr.contains("on Application")){
            put("price", 1);
        } else {
            put("price", 0);
        }
    }

    public String getCoverImage() {
        return getString("coverImage");
    }

    public void setCoverImage(String imageId) {
        if (!imageId.isEmpty()) {
            put("coverImage", "https://www.kahndesign.com/imgLarge/" + imageId + ".jpg");
        } else {
            put("coverImage", "https://www.kahndesign.com/images/AwaitingImage.png");
        }
    }

    public int getYear() {
        return getInt("year");
    }

    public void setYear(String yearStr) {
        int year = Integer.parseInt(
                yearStr.split("/")[0]);
        put("year", year);
    }

    public String getColor() {
        return getString("color");
    }

    public void setColor(String color) {
        put("color", color);
    }

    public int getMileage() {
        return getInt("Mileage");
    }

    public void setMileage(String mileageStr) {
        int mileage = Integer.parseInt(
                mileageStr.replaceAll(",", ""));
        put("mileage", mileage);
    }

    public String getFuelType() {
        return getString("fuelType");
    }

    public String getTransType() {
        return getString("transType");
    }

    public void setFuelAndTrans(String fuelAndTrans) {
        String[] params = fuelAndTrans.split(" : ");
        put("fuelType", params[0]);
        put("transType", params[1]);
    }

    public String getLocation() {
        return getString("location");
    }

    public void setLocation(String location) {
        put("location", location);
    }

    public void setLeftHanded(boolean isLeftHanded) {
        put("isLeftHanded", isLeftHanded);
    }

    public boolean isLeftHanded() {
        return getBoolean("isLeftHanded");
    }

    public String formatPrice(int price) {
        NumberFormat f = NumberFormat.getCurrencyInstance(Locale.UK);
        f.setMaximumFractionDigits(0);
        return f.format(price);
    }
}
