package nl.sjtek.control.data.responses;

import nl.sjtek.control.data.settings.DataCollection;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by wouter on 15-11-16.
 */
public class ResponseTest {

    private static final String exampleResponse = "{\"tv\":{\"type\":\"nl.sjtek.control.data.responses.TVResponse\"},\"music\":{\"song\":{\"artist\":\"The Wombats\",\"title\":\"Give Me A Try\",\"album\":\"Glitterbug\",\"total\":228,\"elapsed\":3,\"albumArt\":\"https://lastfm-img2.akamaized.net/i/u/bbd57b4f204e90e9b11145fc2cc56dd9.png\",\"artistArt\":\"https://lastfm-img2.akamaized.net/i/u/63cea570523f4cf0cc1b4c5a58f49c0f.png\"},\"volume\":0,\"state\":\"STATUS_PAUSED\",\"type\":\"nl.sjtek.control.data.responses.MusicResponse\"},\"sonarr\":{\"upcoming\":[],\"diskUsage\":{\"/tv\":{\"free\":520282685440,\"total\":877085790208},\"/\":{\"free\":85000753152,\"total\":98294312960}},\"type\":\"nl.sjtek.control.data.responses.SonarrResponse\"},\"coffee\":{\"lastTriggered\":0,\"heated\":false,\"type\":\"nl.sjtek.control.data.responses.CoffeeResponse\"},\"temperature\":{\"outside\":8,\"inside\":20,\"humidity\":0.58,\"description\":\"Clear\",\"icon\":\"\",\"type\":\"nl.sjtek.control.data.responses.TemperatureResponse\"},\"nightmode\":{\"enabled\":false,\"nextDisable\":\"Thu Mar 16 07:00:00 GMT 2017\",\"type\":\"nl.sjtek.control.data.responses.NightModeResponse\"},\"screen\":{\"state\":\"FULLSCREEN\",\"header\":\"Sjtek Enterprises presents:\",\"title\":\"Sjtek 2016 fissa\",\"subtitle\":\"\",\"currentTime\":\"Wed Mar 15 18:23:15 GMT 2017\",\"nextTrigger\":\"Sat Dec 31 23:00:00 GMT 2016\",\"type\":\"nl.sjtek.control.data.responses.ScreenResponse\"},\"nfc\":{\"type\":\"nl.sjtek.control.data.responses.NFCResponse\"},\"time\":{\"serverTime\":\"Wed Mar 15 18:23:15 GMT 2017\",\"type\":\"nl.sjtek.control.data.responses.TimeResponse\"},\"lights\":{\"1\":true,\"2\":true,\"3\":false,\"4\":false,\"5\":true,\"6\":false,\"7\":true},\"quotes\":{\"quote\":\"Mwoah, Gertje\",\"type\":\"nl.sjtek.control.data.responses.QuotesResponse\"}}";
    private static final String exampleData = "{\"playlists\":{\"default\":{\"defaultPlaylist\":\"SjtekSjpeellijst\",\"playlists\":{\"SjtekSjpeellijst\":\"spotify:user:1133212423:playlist:2A8r6F6GiLwpBCUQ0ImYKW\",\"Star Wars\":\"spotify:user:1133212423:playlist:50rGPAVdQTfoy3n1f3ba8K\",\"Fallout 3\":\"spotify:user:melnim:playlist:0YuOICv0K5c8zod1SpMl33\",\"Fallout 4\":\"spotify:user:cesarmuela:playlist:1Bz8Xqf5g7I98wEYAfI2Ev\",\"Songs to sing in the shower\":\"spotify:user:spotify:playlist:4TNBeyX7awz89qwtTmh9D4\",\"Top 100\":\"spotify:user:spotify:playlist:4hOKQuZbraPDIfaGbM3lKI\",\"Top 100 Alternative\":\"spotify:user:spotify:playlist:3jtuOxsrTRAWvPPLvlW1VR\"}},\"kevin\":{\"defaultPlaylist\":\"Moist Playlist\",\"playlists\":{\"Moist Playlist\":\"spotify:user:1130395265:playlist:7nU3uxnfwhUQFM4ZhANPbF\",\"sjpeellist\":\"spotify:user:1130395265:playlist:5UOGVcoR34i1XUFLYCXbnz\",\"\":\"\"}},\"wouter\":{\"defaultPlaylist\":\"Opzich nog meer wel leuk\",\"playlists\":{\"Opzich wel leuk\":\"spotify:user:1133212423:playlist:75lLEqfsZ0lR1L8xz61TnB\",\"Opzich nog meer wel leuk\":\"spotify:user:1133212423:playlist:2x3UOU4pCPF1Fg3QRUr4B5\",\"Redelijk kalm\":\"spotify:user:1133212423:playlist:6clH0v0FfHyjsEiHgULafH\",\"Discover Weely\":\"spotify:user:spotifydiscover:playlist:2KDeo7tPfy0cDs18HonK5A\"}},\"tijn\":{\"defaultPlaylist\":\"Swek muziek\",\"playlists\":{\"Swek muziek\":\"spotify:user:1123840057:playlist:1kbSO9MqJMWOdsIfPhjcvW\",\"Morning Chillout\":\"spotify:user:1123840057:playlist:4pykd6PiFVzMPTOSwmgICL\",\"The Internship\":\"spotify:user:1123840057:playlist:4cfMnEqS1mwCRm09iyo5DU\",\"Coding Rave\":\"spotify:user:1123840057:playlist:78nuAkhrGgF7rYbVBSj2t5\",\"Diepe Huis\":\"spotify:user:1123840057:playlist:2AdY8mQLAbHOFYkQ7TMitk\",\"The Magical Ultimate LAN Party Playlist\":\"yt:https://www.youtube.com/watch?v\\u003dFy68No-L6Pg\\u0026list\\u003dPLmAlZturrQCVb3djt3ysDIb5aqAO2POHK\"}}},\"users\":{\"default\":{\"nickNames\":[\"\"],\"playlistSet\":{\"defaultPlaylist\":\"SjtekSjpeellijst\",\"playlists\":{\"SjtekSjpeellijst\":\"spotify:user:1133212423:playlist:2A8r6F6GiLwpBCUQ0ImYKW\",\"Star Wars\":\"spotify:user:1133212423:playlist:50rGPAVdQTfoy3n1f3ba8K\",\"Fallout 3\":\"spotify:user:melnim:playlist:0YuOICv0K5c8zod1SpMl33\",\"Fallout 4\":\"spotify:user:cesarmuela:playlist:1Bz8Xqf5g7I98wEYAfI2Ev\",\"Songs to sing in the shower\":\"spotify:user:spotify:playlist:4TNBeyX7awz89qwtTmh9D4\",\"Top 100\":\"spotify:user:spotify:playlist:4hOKQuZbraPDIfaGbM3lKI\",\"Top 100 Alternative\":\"spotify:user:spotify:playlist:3jtuOxsrTRAWvPPLvlW1VR\"}},\"nfcTags\":[\"13376969\"],\"greetings\":[[\"Good night %s.\"],[\"Good morning %s.\"],[\"Good afternoon %s.\"],[\"Good evening %s.\"]],\"farewells\":[[\"Sleep well, %s.\"],[\"Good luck today, %s.\"],[\"Good bye, %s.\"],[\"Have a nice evening, %s.\"]],\"checkExtraLight\":false,\"injectTaylorSwift\":true,\"autoStartMusic\":false},\"kevin\":{\"nickNames\":[\"kevin\"],\"playlistSet\":{\"defaultPlaylist\":\"Moist Playlist\",\"playlists\":{\"Moist Playlist\":\"spotify:user:1130395265:playlist:7nU3uxnfwhUQFM4ZhANPbF\",\"sjpeellist\":\"spotify:user:1130395265:playlist:5UOGVcoR34i1XUFLYCXbnz\",\"\":\"\"}},\"nfcTags\":[\"281846010\"],\"greetings\":[[\"Good night %s.\"],[\"Good morning %s.\"],[\"Good afternoon %s.\",\"I hope you didn\\u0027t burn anything down, %s.\"],[\"Good evening %s.\"]],\"farewells\":[[\"Sleep well, %s.\"],[\"Try not to burn the world, %s.\",\"Have a nice day, %s.\"],[\"Good bye, %s.\"],[\"Have a nice evening, %s.\"]],\"checkExtraLight\":false,\"injectTaylorSwift\":false,\"autoStartMusic\":false},\"tijn\":{\"nickNames\":[\"3D\",\"master renders\"],\"playlistSet\":{\"defaultPlaylist\":\"Swek muziek\",\"playlists\":{\"Swek muziek\":\"spotify:user:1123840057:playlist:1kbSO9MqJMWOdsIfPhjcvW\",\"Morning Chillout\":\"spotify:user:1123840057:playlist:4pykd6PiFVzMPTOSwmgICL\",\"The Internship\":\"spotify:user:1123840057:playlist:4cfMnEqS1mwCRm09iyo5DU\",\"Coding Rave\":\"spotify:user:1123840057:playlist:78nuAkhrGgF7rYbVBSj2t5\",\"Diepe Huis\":\"spotify:user:1123840057:playlist:2AdY8mQLAbHOFYkQ7TMitk\",\"The Magical Ultimate LAN Party Playlist\":\"yt:https://www.youtube.com/watch?v\\u003dFy68No-L6Pg\\u0026list\\u003dPLmAlZturrQCVb3djt3ysDIb5aqAO2POHK\"}},\"nfcTags\":[\"552518698\",\"6334480\"],\"greetings\":[[\"Good night %s.\"],[\"Good morning %s.\"],[\"Good afternoon %s.\"],[\"Good evening %s.\"]],\"farewells\":[[\"Sleep well, %s.\"],[\"May the force be with you, %s.\"],[\"Good bye, %s.\"],[\"Have a nice evening, %s.\"]],\"checkExtraLight\":false,\"injectTaylorSwift\":false,\"autoStartMusic\":false},\"wouter\":{\"nickNames\":[\"sir wouter\",\"lord habets\"],\"playlistSet\":{\"defaultPlaylist\":\"Opzich nog meer wel leuk\",\"playlists\":{\"Opzich wel leuk\":\"spotify:user:1133212423:playlist:75lLEqfsZ0lR1L8xz61TnB\",\"Opzich nog meer wel leuk\":\"spotify:user:1133212423:playlist:2x3UOU4pCPF1Fg3QRUr4B5\",\"Redelijk kalm\":\"spotify:user:1133212423:playlist:6clH0v0FfHyjsEiHgULafH\",\"Discover Weely\":\"spotify:user:spotifydiscover:playlist:2KDeo7tPfy0cDs18HonK5A\"}},\"nfcTags\":[\"1853719819\",\"16514020840\",\"48115222160\"],\"greetings\":[[\"Good night %s.\"],[\"Good morning %s.\"],[\"Good afternoon %s.\"],[\"Good to see you alive and well, %s.\"]],\"farewells\":[[\"Sleep well, %s.\"],[\"Good luck today, %s.\"],[\"Good bye, %s.\"],[\"Have a nice evening, %s.\"]],\"checkExtraLight\":true,\"injectTaylorSwift\":false,\"autoStartMusic\":false}},\"quotes\":[\"Alleen massaproductie\",\"Dien mam\",\"Mwoah, Gertje\",\"Analytsamson\",\"Een frietkraam dat geen frieten verkoopt\",\"Moet hebben, afblijven\",\"Sjtek meester ras\",\"Ik heb je liefdestruffel naar binnen geramt\",\"Ja joa\",\"10/10 would yolo again\",\"Once you go bluffer, you will suffer\",\"#stage\",\"Ze is zo nat dat ze van mijn bed af glijd.\",\"Helikopterpiloot\",\"Chiquita bananen meester ras\",\"Tet is wet\"]}";

    @Test
    public void responseJson() {
        ResponseCollection responseCollection = new ResponseCollection(exampleResponse);
        Assert.assertEquals(responseCollection.getMusic().getSong().getArtist(), "The Wombats");
        Assert.assertEquals(responseCollection.getLights().isLight1(), true);
        Assert.assertEquals(responseCollection.getQuotes().getQuote(), "Mwoah, Gertje");
    }

    @Test
    public void responseSerialize() throws IOException {
        ResponseCollection responseCollection = new ResponseCollection(exampleResponse);
        serialize(responseCollection);
    }

    @Test
    public void dataJson() {
        DataCollection.fromJson(exampleData);
    }

    @Test
    public void serializeData() throws IOException {
        DataCollection data = DataCollection.fromJson(exampleData);
        serialize(data);

    }

    private void serialize(Serializable serializable) throws IOException {
        new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(serializable);
    }
}