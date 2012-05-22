/**
 * JavaScript functions for front-end display of webform conditional components
 */
(function ($) {
	
Drupal.behaviors.webform_conditional = Drupal.behaviors.webform_conditional || {};
Drupal.behaviors.webform_conditional.attach = function() {
	// create quasi static var to save perfomance
	Drupal.webform_conditional.wrappers = new Object();
	Drupal.webform_conditional.components = new Object();
	$.each(Drupal.settings.webform_conditional.fields, function(triggerField_key, triggerField_info) {
		
		var formItemWrapper = Drupal.webform_conditional.getWrapper(triggerField_info);
		if(formItemWrapper.length > 0){
				// Add onclick handler to Parent field
				Drupal.webform_conditional.addOnChange (triggerField_key, triggerField_info);
		}
		});
	// after all added - trigger initial
	$.each(Drupal.settings.webform_conditional.fields, function(triggerField_key, triggerField_info) {
		var formItemWrapper = Drupal.webform_conditional.getWrapper(triggerField_info);
			if(formItemWrapper.length > 0){
				var field_name = Drupal.webform_conditional.escapeId(triggerField_key);
				var components = Drupal.webform_conditional.getComponentsByName(field_name);
				if(components.attr('type')=='radio' || components.attr('type')=='checkbox'){
					$(components[0]).triggerHandler('click');
				}else{
					components.triggerHandler('change');
				}
			}
		});
	return;
};
Drupal.webform_conditional = Drupal.webform_conditional || {};
// create quasi static var to save perfomance
Drupal.webform_conditional.getWrapper = function(fieldInfo){
	if(Drupal.webform_conditional.wrappers[fieldInfo['css_id']]){
		return Drupal.webform_conditional.wrappers[fieldInfo['css_id']];
	}
	return Drupal.webform_conditional.wrappers[fieldInfo['css_id']] = $("#" + fieldInfo['css_id']);
};
Drupal.webform_conditional.addOnChange = function(triggerField_key, triggerField_info) {
	var monitor_field_name = Drupal.webform_conditional.escapeId(triggerField_key);
	var changeFunction = function() {
		
		Drupal.webform_conditional.setVisibility(triggerField_key,triggerField_info);
	};
	$.each(triggerField_info['dependent_fields'],function(dependent_field_key,dependent_field_info){
		var formItemWrapper = Drupal.webform_conditional.getWrapper(dependent_field_info);
	    if(formItemWrapper.length > 0){
	            formItemWrapper.css("display", "none");
	    }

	});
	var components = Drupal.webform_conditional.getComponentsByName(monitor_field_name);
	if(components.attr('type')=='radio' || components.attr('type')=='checkbox'){
		components.click(changeFunction)
	}else{
		components.change(changeFunction);
	}
	
};
Drupal.webform_conditional.setVisibility = function(triggerField_key,triggerField_info,monitorField,monitorInfo){
	var monitor_field_name = Drupal.webform_conditional.escapeId(triggerField_key);
	var currentValues = Drupal.webform_conditional.getFieldValue(monitor_field_name); 
	var monitor_visible = true;
	if(monitorField !== undefined){
		monitor_visible = Drupal.webform_conditional.getWrapper(monitorInfo).data('wfc_visible');
	}
	$.each(triggerField_info['dependent_fields'],function(dependentField,dependentInfo){
		if(((dependentInfo['operator'] == "=" && !Drupal.webform_conditional.Matches(currentValues,dependentInfo['monitor_field_value']))
			|| (dependentInfo['operator'] == "!=" && Drupal.webform_conditional.Matches(currentValues,dependentInfo['monitor_field_value']))) 
			|| !monitor_visible){
				// show the hidden div
				// have to set wfc_visible so that you check the visibility of this
				// immediately
			 Drupal.webform_conditional.getWrapper(dependentInfo).hide().data('wfc_visible',false);
		}else {
				// otherwise, hide it
			Drupal.webform_conditional.getWrapper(dependentInfo).show().data('wfc_visible',true);
				// and clear data (using different selector: want the
				// textarea to be selected, not the parent div)
		}
		Drupal.webform_conditional.TriggerDependents(dependentField,dependentInfo);
	});
};
Drupal.webform_conditional.getComponentsByName = function (field_name){
	// check to save jquery calls
	if(Drupal.webform_conditional.components[field_name]){
		return Drupal.webform_conditional.components[field_name];
	}
	// don't overwrite original name to store for caching
	var css_field_name = "[" + field_name + "]";
	var nid = Drupal.settings.webform_conditional.nid;
	if(nid instanceof Array){
		nid = Drupal.settings.webform_conditional.nid[0];
	}
	return Drupal.webform_conditional.components[field_name] = $("#webform-client-form-" + nid + " *[name*='"+css_field_name+"']");
};
Drupal.webform_conditional.TriggerDependents = function(monitorField,monitorInfo){
	$.each(Drupal.settings.webform_conditional.fields, function(triggerField_key, triggerField_info) {
		if(triggerField_key == monitorField){
			Drupal.webform_conditional.setVisibility(triggerField_key, triggerField_info,monitorField,monitorInfo);
		};
	});
};
Drupal.webform_conditional.getFieldValue = function(field_name){
	field_name = "[" + field_name + "]";
	var selected = [];
	var vals = [];
	if($('form input[name*="'+field_name+'"]:checked').length >= 1){
		selected =  $('form input[name*="'+field_name+'"]:checked');
	}else if($('form select[name*="'+field_name+'"] option:selected').length >= 1){
		selected = $('form select[name*="'+field_name+'"] option:selected');
	}
	if(selected.length == 0){
		return vals;
	}
	selected.each(function(i){
	     vals[i] = $(this).val();
	    });
	return vals;
};
Drupal.webform_conditional.Matches = function(currentValues,triggerValues){
	var found = false;
	$.each(triggerValues, function(index, value) { 
		  if(jQuery.inArray(value,currentValues)> -1){
			  found = true;
			  return false;
		  }
		});
	return found;
};
// Drupal.webform_conditional.escapeId
Drupal.webform_conditional.escapeId = function(myid) {
	if (typeof myid == 'undefined') {
		return;
	}
	   return  myid.replace(/(:|\.)/g,'\\$1');
};
})(jQuery);