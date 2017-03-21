package test.ui;

import game.ui.StartUI;
import game.ui.UI;
import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by jackm
 */
public class StartUITest {

    public StartUI start;
    public UI ui;

    @Before
    public void setUp() throws Exception {
        //start = new StartUI(ui);
    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void createUI() throws Exception {
        assertEquals(1, 1);
    }

}
