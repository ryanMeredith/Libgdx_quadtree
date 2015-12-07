package uk.co.adeveloperabroad.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import uk.co.adeveloperabroad.QuadTreeMain;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(860, 640);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new QuadTreeMain();
        }
}