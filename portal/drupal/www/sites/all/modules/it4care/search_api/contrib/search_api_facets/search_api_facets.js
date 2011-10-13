// $Id: search_api.admin.js,v 1.1.2.4 2010/11/07 17:24:16 drunkenmonkey Exp $

(function($) {

Drupal.behaviors.searchApiFacetsMore = {
  attach: function (context, settings) {
    $('.block-search-api-facets', context).has('ul.search-api-facets > li.element-hidden').each(function() {
      $block = $(this);
      $('.content', $block).append($('<a href="#" id="' + $block.attr('id') + '-more-link" class="search-api-facet-more-link">' + Drupal.t('more') + '</a>')
        .attr('title', Drupal.t('Show additional facet terms.'))
        .click(function() {
          $link = $(this);
          if ($link.hasClass('less')) {
            $('#' + $link.attr('id').replace(/-more-link$/, '') + ' li.search-api-facet-link-additional', context).addClass('element-hidden');
            $link.text(Drupal.t('more'));
            $link.removeClass('less');
          }
          else {
            $('#' + $link.attr('id').replace(/-more-link$/, '') + ' li.search-api-facet-link-additional', context).removeClass('element-hidden');
            $link.text(Drupal.t('less'));
            $link.addClass('less');
          }
          return false;
        })
      );
    });
  }
};

})(jQuery);