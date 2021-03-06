package org.primefaces.extensions.component.gchart;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIOutput;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;

import com.google.gson.JsonArray;
import org.primefaces.component.api.Widget;
import org.primefaces.event.SelectEvent;
import org.primefaces.extensions.util.json.GsonConverter;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;

@ResourceDependencies({
         @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
         @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
         @ResourceDependency(library = "primefaces", name = "core.js"),
         @ResourceDependency(library = "primefaces-extensions", name = "primefaces-extensions.css"),
         @ResourceDependency(library = "primefaces-extensions", name = "primefaces-extensions.js")
})
public class GChart extends UIOutput implements Widget, ClientBehaviorHolder {

   public static final String COMPONENT_TYPE = "org.primefaces.extensions.component.GChart";
   public static final String COMPONENT_FAMILY = "org.primefaces.extensions.component";
   private static final String DEFAULT_RENDERER = "org.primefaces.extensions.component.GChartRenderer";

   static final String DEFAULT_TYPE = "select";
   private static final Collection<String> EVENT_NAMES = Collections
            .unmodifiableCollection(Arrays.asList(DEFAULT_TYPE));

   protected static enum PropertyKeys {
      widgetVar, 
      width, 
      height, 
      title;
   }

   public GChart() {
      setRendererType(DEFAULT_RENDERER);
   }

   @Override
   public String getFamily() {
      return COMPONENT_FAMILY;
   }

   @Override
   public Collection<String> getEventNames() {
      return EVENT_NAMES;
   }

   @Override
   public String getDefaultEventName() {
      return DEFAULT_TYPE;
   }

   public String getWidgetVar() {
      return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
   }

   public void setWidgetVar(String _widgetVar) {
      getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
   }

   public Integer getWidth() {
      return (Integer) this.getStateHelper().eval(PropertyKeys.width, null);
   }

   public void setWidth(Integer width) {
      this.getStateHelper().put(PropertyKeys.width, width);
   }

   public Integer getHeight() {
      return (Integer) this.getStateHelper().eval(PropertyKeys.height, null);
   }

   public void setHeight(Integer width) {
      this.getStateHelper().put(PropertyKeys.height, width);
   }

   public String getTitle() {
      return (String) getStateHelper().eval(PropertyKeys.title, null);
   }

   public void setTitle(String title) {
      getStateHelper().put(PropertyKeys.title, title);
   }

   @Override
   public String resolveWidgetVar() {
      return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
   }

   @Override
   public void queueEvent(FacesEvent event) {

      FacesContext context = getFacesContext();
      if (isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
         Map<String, String> params = context.getExternalContext().getRequestParameterMap();
         String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

         if (eventName.equals("select")) {
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            String clientId = this.getClientId(context);

            Object value = GsonConverter.getGson().fromJson(params.get(clientId + "_hidden").toString(),
                     JsonArray.class);

            SelectEvent selectEvent = new SelectEvent(this, behaviorEvent.getBehavior(), value);
            selectEvent.setPhaseId(behaviorEvent.getPhaseId());

            super.queueEvent(selectEvent);
         }
      } else {
         super.queueEvent(event);
      }
   }

   public boolean isRequestSource(FacesContext context) {
      String partialSource = context.getExternalContext().getRequestParameterMap()
               .get(Constants.RequestParams.PARTIAL_SOURCE_PARAM);

      return this.getClientId(context).equals(partialSource);
   }
}
