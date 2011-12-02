
(function ($) {
  Drupal.behaviors.piwik_reports = {
    attach: function(context, settings) {

      var pk_url = settings.piwik_reports.url
      var data
      var header = "<table class='sticky-enabled sticky-table'><tbody></tbody></table>";
      var item
      
      // Add the table and show "Loading data..." status message for long running requests.
      $("#piwikpageviews").html(header);
      $("#piwikpageviews > table > tbody").html("<tr><td>" + Drupal.t('Loading data...') + "</td></tr>");

      // Get data from remote Piwik server.
      $.getJSON(pk_url, function(data){ 
        $.each(data, function(key, val) {
          item = val;
        });
        var pk_content = "";
        if (item != undefined) {
          if (item.nb_visits) {
            pk_content += "<tr><td>" + Drupal.t('Visits') + "</td>";
            pk_content += "<td>" + item.nb_visits + "</td></tr>" ;
          }
          if (item.nb_hits) {
            pk_content += "<tr><td>" + Drupal.t('Page views') + "</td>";
            pk_content += "<td>" + item.nb_hits + "</td></tr>" ;
          }
        }
        // Push data into table and replace "Loading data..." status message.
        if (pk_content) {
          $("#piwikpageviews > table > tbody").html(pk_content);
        }
        else {
          $("#piwikpageviews > table > tbody > tr > td").html(Drupal.t('No data available.'));
        }
      });
   
    }

  };

})(jQuery);
