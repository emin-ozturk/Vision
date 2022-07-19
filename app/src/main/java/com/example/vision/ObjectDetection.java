package com.example.vision;

import android.annotation.SuppressLint;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SuppressLint("Registered")
public class ObjectDetection extends AppCompatActivity {
    private Net net;
    private List<String> cocoNamesText;
    private List<String> cocoNamesSound;
    private int[][] colors;
    private boolean isObject; //Görüntüde nesne var mı
    private int getWidth, getHeight;
    private int size;
    private String objectName;

    ObjectDetection(int getWidth, int getHeight, String defaultLanguage) {
        coconames(defaultLanguage); //Kelimeler
        randomColor(); //Rasgele renk
        isObject = false;

        this.getWidth = getWidth;
        this.getHeight = getHeight;

        size = getWidth / 1080;
        if (2 > size) size = 1;
        if (5 < size) size = 5;

        objectName = "";
    }

    private void loadingYolo() {
        if (net == null) {
            String cfg = Environment.getExternalStorageDirectory() + "/Vision/dnns/yolov3-tiny.cfg";
            String weights = Environment.getExternalStorageDirectory() + "/Vision/dnns/yolov3-tiny.weights";

            net = Dnn.readNetFromDarknet(cfg, weights);
        }
    }

    private void coconames(String defaultLanguage) {
        switch (defaultLanguage) {
            case "Türkçe":
                cocoNamesText = Arrays.asList("insan", "bisiklet", "araba", "motosiklet", "ucak", "otobus", "tren", "kamyon",
                        "tekne", "trafik isigi", "yangin muslugu", "dur isareti", "parkmetre", "bank", "kus", "kedi",
                        "kopek", "at", "koyun", "inek", "fil", "ayi", "zebra", "zurafa", "sirt cantası", "semsiye",
                        "el cantasi", "kravat", "bavul", "frizbi", "kayak", "snowboard", "spor topu", "ucurtma", "beysbol sopasi",
                        "beyzbol eldiveni", "kaykay", "sorf tahtasi", "tenis raketi", "sise", "sarap kadehi", "bardak", "catal",
                        "bicak", "kasik", "kase", "muz", "elma", "sandvic", "portakal", "brokoli", "havuc", "sosisli sandvic",
                        "pizza", "donut", "kek", "sandalye", "kanepe", "saksi", "yatak", "yemek masasi", "tuvalet", "televizyon",
                        "dizustu bilgisayar", "fare", "kumanda", "klavye", "cep telefonu", "mikrodalga", "firin", "ekmek kızartma makinesi",
                        "lavabo", "buzdolabi", "kitap", "saat", "vazo", "makas", "oyuncak ayi", "sac kurutma makinası", "dis fircasi");

                cocoNamesSound = Arrays.asList("insan", "bisiklet", "araba", "motosiklet", "uçak", "otobüs", "tren", "kamyon",
                        "tekne", "trafik ışığı", "yangın musluğu", "dur işareti", "parkmetre", "bank", "kuş", "kedi",
                        "köpek", "at", "koyun", "inek", "fil", "ayı", "zebra", "zürafa", "sırt çantası", "şemsiye",
                        "el çantası", "kravat", "bavul", "frizbi", "kayak", "snowboard", "spor topu", "uçurtma", "beysbol sopası",
                        "beyzbol eldiveni", "kaykay", "sörf tahtası", "tenis raketi", "şişe", "şarap kadehi", "bardak", "çatal",
                        "bıçak", "kaşık", "kase", "muz", "elma", "sandviç", "portakal", "brokoli", "havuç", "sosisli sandviç",
                        "pizza", "donut", "kek", "sandalye", "kanepe", "saksı", "yatak", "yemek masası", "tuvalet", "televizyon",
                        "dizüstü bilgisayar", "fare", "uzaktan", "klavye", "cep telefonu", "mikrodalga", "fırın", "ekmek kızartma makinesi",
                        "lavabo", "buzdolabı", "kitap", "saat", "vazo", "makas", "oyuncak ayı", "saç kurutma makinası", "diş fırçası");
                break;
            case "English":
                cocoNamesText = Arrays.asList("person", "bicycle", "car", "motorbike", "airplane", "bus", "train", "truck",
                        "boat", "traffic light", "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat",
                        "dog", "horse", "sheep", "cow", "elephant", "bear", "zebra", "giraffe", "backpack", "umbrella",
                        "handbag", "tie", "suitcase", "frisbee", "skis", "snowboard", "sports ball", "kite", "baseball bat",
                        "baseball glove", "skateboard", "surfboard", "tennis racket", "bottle", "wine glass", "cup", "fork",
                        "knife", "spoon", "bowl", "banana", "apple", "sandwich", "orange", "broccoli", "carrot", "hot dog",
                        "pizza", "donut", "cake", "chair", "sofa", "potted plant", "bed", "dining table", "toilet", "tv monitor",
                        "laptop", "mouse", "remote", "keyboard", "cell phone", "microwave", "oven", "toaster", "sink", "refrigerator",
                        "book", "clock", "vase", "scissors", "teddy bear", "hair drier", "toothbrush");

                cocoNamesSound = Arrays.asList("person", "bicycle", "car", "motorbike", "airplane", "bus", "train", "truck",
                        "boat", "traffic light", "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat",
                        "dog", "horse", "sheep", "cow", "elephant", "bear", "zebra", "giraffe", "backpack", "umbrella",
                        "handbag", "tie", "suitcase", "frisbee", "skis", "snowboard", "sports ball", "kite", "baseball bat",
                        "baseball glove", "skateboard", "surfboard", "tennis racket", "bottle", "wine glass", "cup", "fork",
                        "knife", "spoon", "bowl", "banana", "apple", "sandwich", "orange", "broccoli", "carrot", "hot dog",
                        "pizza", "donut", "cake", "chair", "sofa", "potted plant", "bed", "dining table", "toilet", "tv monitor",
                        "laptop", "mouse", "remote", "keyboard", "cell phone", "microwave", "oven", "toaster", "sink", "refrigerator",
                        "book", "clock", "vase", "scissors", "teddy bear", "hair drier", "toothbrush");
                break;
            case "Français":
                cocoNamesText = Arrays.asList("humain", "velo", "voiture", "moto", "avion", "bus", "train", "camion",
                        "bateau", "feu de circulation", "bouche d'incendie", "panneau d'arret", "parcmetre", "banc", "oiseau", "chat",
                        "chien", "cheval", "mouton", "vache", "elephant", "ours", "zebre", "girafe", "sac size dos", "parapluie",
                        "sac a main", "cravate", "valise", "frisbee", "skis", "snowboard", "ballon de sport", "cerf-volant", "batte de baseball",
                        "gant de baseball", "planche a roulettes", "planche de surf", "raquette de tennis", "bouteille", "verre a vin", "verre", "fourchette",
                        "couteau", "cuillere", "bol", "banane", "pomme", "sandwich", "orange", "brocoli", "carotte", "hot dog",
                        "pizza", "beignet", "gateau", "chaise", "canape", "pot de fleur", "lit", "table à manger", "toilette", "television",
                        "ordinateur", "souris", "telecommande", "clavier", "telephone portable", "micro-ondes", "four", "grille-pain",
                        "evier", "refrigerateur", "livre", "horloge", "vase", "ciseaux", "ours en peluche", "seche-cheveux", "brosse size dents");

                cocoNamesSound = Arrays.asList("humain", "vélo", "voiture", "moto", "avion", "bus", "train", "camion",
                        "bateau", "feu de circulation", "bouche d'incendie", "panneau d'arrêt", "parcmètre", "banc", "oiseau", "chat",
                        "chien", "cheval", "mouton", "vache", "éléphant", "ours", "zèbre", "girafe", "sac à dos", "parapluie",
                        "sac a main", "cravate", "valise", "frisbee", "skis", "snowboard", "ballon de sport", "cerf-volant", "batte de baseball",
                        "gant de baseball", "planche a roulettes", "planche de surf", "raquette de tennis", "bouteille", "verre à vin", "verre", "fourchette",
                        "couteau", "cuillère", "bol", "banane", "pomme", "sandwich", "orange", "brocoli", "carotte", "hot dog",
                        "pizza", "beignet", "gâteau", "chaise", "canapé", "pot de fleur", "lit", "table à manger", "toilette", "television",
                        "ordinateur", "souris", "télécommande", "clavier", "téléphone portable", "micro-ondes", "four", "grille-pain",
                        "évier", "réfrigérateur", "livre", "horloge", "vase", "ciseaux", "ours en peluche", "sèche-cheveux", "brosse à dents");
                break;
            case "Deutsch":
                cocoNamesText = Arrays.asList("person", "fahrrad", "auto", "motorrad", "flugzeug", "bus", "zug", "lkw",
                        "boot", "ampel", "hydrant", "stoppschild", "parkuhr", "bank", "vogel", "katze",
                        "hund", "pferd", "schaf", "kuh", "elefant", "bar", "zebra", "giraffe", "rucksack", "regenschirm",
                        "handtasche", "krawatte", "koffer", "frisbee", "ski", "snowboard", "sportball", "drachen", "baseballschlager",
                        "baseballhandschuh", "skateboard", "surfbrett", "tennisschläger", "flasche", "weinglas", "glas", "gabel",
                        "messer", "loffel", "schussel", "banane", "apfel", "sandwich", "orange", "brokkoli", "karotte", "hot dog",
                        "pizza", "donut", "kuchen", "stuhl", "sofa", "blumentopf", "bett", "esstisch", "toilette", "fernsehmonitor",
                        "laptop", "maus", "fernbedienung", "tastatur", "mobiltelefon", "mikrowelle", "backofen", "toaster",
                        "waschbecken", "kühlschrank", "buch", "stunde", "vase", "schere", "teddybar", "fon", "zahnburste");

                cocoNamesSound = Arrays.asList("person", "fahrrad", "auto", "motorrad", "flugzeug", "bus", "zug", "lkw",
                        "boot", "ampel", "hydrant", "stoppschild", "parkuhr", "bank", "vogel", "katze",
                        "hund", "pferd", "schaf", "kuh", "elefant", "bär", "zebra", "giraffe", "rucksack", "regenschirm",
                        "handtasche", "krawatte", "koffer", "frisbee", "ski", "snowboard", "sportball", "drachen", "baseballschläger",
                        "baseballhandschuh", "skateboard", "surfbrett", "tennisschläger", "flasche", "weinglas", "tglasasse", "gabel",
                        "messer", "löffel", "schüssel", "banane", "apfel", "sandwich", "orange", "brokkoli", "karotte", "hot dog",
                        "pizza", "donut", "kuchen", "stuhl", "sofa", "topfpflanze", "bett", "esstisch", "toilette", "fernsehmonitor",
                        "laptop", "maus", "fernbedienung", "tastatur", "mobiltelefon", "mikrowelle", "backofen", "toaster",
                        "waschbecken", "kühlschrank", "buch", "stunde", "vase", "schere", "teddybär", "fön", "zahnbürste");
                break;
            case "Español":
                cocoNamesText = Arrays.asList("persona", "bicicleta", "automovil", "moto", "avion", "autobus", "tren", "camion",
                        "bote", "semaforo", "boca de incendios", "senal de stop", "parquimetro", "banco", "pajaro", "gato",
                        "perro", "caballo", "oveja", "vaca", "elefante", "oso", "cebra", "jirafa", "mochila", "paraguas",
                        "bolso", "corbata", "maleta", "frisbee", "esquís", "snowboard", "pelota deportiva", "cometa", "bate de beisbol",
                        "guante de beisbol", "patineta", "tabla de surf", "raqueta de tenis", "botella", "copa de vino", "copa", "tenedor",
                        "cuchillo", "cuchara", "tazon", "platano", "manzana", "sandwich", "naranja", "brocoli", "zanahoria", "hot dog",
                        "pizza", "donut", "pastel", "silla", "sofa", "planta en maceta", "cama", "mesa de comedor", "inodoro", "monitor de television",
                        "computadora portatil", "mouse", "remoto", "teclado", "telefono celular", "microondas", "horno", "tostador", "fregadero", "refrigerador",
                        "libro", "reloj", "florero", "tijeras", "oso de peluche", "secador de pelo", "cepillo de dientes");

                cocoNamesSound = Arrays.asList("persona", "bicicleta", "automóvil", "moto", "avión", "autobús", "tren", "camión",
                        "bote", "semáforo", "boca de incendios", "señal de stop", "parquímetro", "banco", "pájaro", "gato",
                        "perro", "caballo", "oveja", "vaca", "elefante", "oso", "cebra", "jirafa", "mochila", "paraguas",
                        "bolso", "corbata", "maleta", "frisbee", "esquís", "snowboard", "pelota deportiva", "cometa", "bate de béisbol",
                        "guante de béisbol", "patineta", "tabla de surf", "raqueta de tenis", "botella", "copa de vino", "copa", "tenedor",
                        "cuchillo", "cuchara", "tazón", "plátano", "manzana", "sándwich", "naranja", "brócoli", "zanahoria", "hot dog",
                        "pizza", "donut", "pastel", "silla", "sofá", "planta en maceta", "cama", "mesa de comedor", "inodoro", "monitor de televisión",
                        "computadora portátil", "mouse", "remoto", "teclado", "teléfono celular", "microondas", "horno", "tostador", "fregadero", "refrigerador",
                        "libro", "reloj", "florero", "tijeras", "oso de peluche", "secador de pelo", "cepillo de dientes");
                break;
        }
    }

    private void randomColor() {
        Random random = new Random();
        colors = new int[80][3];
        for (int i = 0; i < 80; i++) {
            int r = random.nextInt(255);
            int g = random.nextInt(255);
            int b = random.nextInt(255);

            colors[i][0] = r;
            colors[i][1] = g;
            colors[i][2] = b;
        }
    }

    public Mat detectionYolo(Mat imageMat) {
        loadingYolo();

        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_RGBA2RGB);
        Size yoloSize = new Size(416, 416);
        Mat blob = Dnn.blobFromImage(imageMat, 0.00392, yoloSize, new Scalar(0), false, false);
        net.setInput(blob);

        List<Mat> result = new ArrayList<>(2);
        List<String> outBlobNames = new ArrayList<>();

        //yolov3-tiny katmanları
        outBlobNames.add(0, "yolo_16");
        outBlobNames.add(1, "yolo_23");
        net.forward(result, outBlobNames);

        float thresholdValue = 0.3f; //Eşik değer

        List<Integer> objectIds = new ArrayList<>(); //Bulunan nesnenin sırası

        List<Float> confs = new ArrayList<>(); //Bulunan nesnelerin benzeme oranları

        List<Rect> rectCoordinates = new ArrayList<>(); //Kutuların koordinatları

        for (int i = 0; i < result.size(); i++) {
            Mat level = result.get(i);
            for (int j = 0; j < level.rows(); j++) {
                Mat row = level.row(j);
                Mat scores = row.colRange(5, level.cols());
                Core.MinMaxLocResult mm = Core.minMaxLoc(scores);

                //Benzeme oranı
                float rate = (float) mm.maxVal;
                Point classIdPoint = mm.maxLoc;

                //Kutu korodinatları
                if (rate > thresholdValue) {
                    int centerX = (int) (row.get(0, 0)[0] * imageMat.cols());
                    int centerY = (int) (row.get(0, 1)[0] * imageMat.rows());
                    int width = (int) (row.get(0, 2)[0] * imageMat.cols());
                    int height = (int) (row.get(0, 3)[0] * imageMat.rows());
                    int left = (centerX - width / 2) < 0 ? 10 : (centerX - width / 2);
                    int top = (centerY - height / 2) < 0 ? 20 : (centerY - height / 2);
                    if (top + height > getHeight) { height -= (top + height) - getHeight + 10; }
                    if (left + width > getWidth) { width -= (left + width) - getWidth + 10; }

                    objectIds.add((int) classIdPoint.x);
                    confs.add(rate);
                    rectCoordinates.add((new Rect(left, top, width, height)));
                }
            }
        }

        if (confs.size() >= 1) {
            float nmsTresh = 0.2f;

            MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confs));
            MatOfRect boxes = new MatOfRect(rectCoordinates.toArray(new Rect[0]));
            MatOfInt indices = new MatOfInt();
            Dnn.NMSBoxes(boxes, confidences, thresholdValue, nmsTresh, indices);

            //Kutu çizme
            int[] ind = indices.toArray();
            for (int idx : ind) {
                Rect box = rectCoordinates.toArray(new Rect[0])[idx];

                int idGuy = objectIds.get(idx);

                //Yazıların ekran dışına çıkmaması için
                Point textLocalation;
                if (box.tl().y - 25 < 0) { textLocalation = new Point(box.tl().x, box.tl().y + 25); }
                else { textLocalation = new Point(box.tl().x, box.tl().y - 5); }

                Scalar color = new Scalar(colors[idGuy][0], colors[idGuy][1], colors[idGuy][2]);
                int font = Core.FONT_HERSHEY_SIMPLEX;
                Imgproc.rectangle(imageMat, box.tl(), box.br(), color, size * 2);
                Imgproc.putText(imageMat, cocoNamesText.get(idGuy), textLocalation, font, size, color, (size * 2));
                isObject = true;
                objectName += cocoNamesSound.get(idGuy) + ". ";
            }
        } else {
            isObject = false;
        }
        return imageMat;
    }

    public boolean getIsObject() {
        return isObject;
    }

    public String getObjectName() {
        return objectName;
    }
}
