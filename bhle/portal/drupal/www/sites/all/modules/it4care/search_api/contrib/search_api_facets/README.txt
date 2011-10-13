
Search facets
-------------

This module allows you to create facetted searches for any search executed via
the Search API, no matter if executed by a search page, a view or any other
module. The only thing you'll need is a search service class that supports the
"search_api_facets" feature. Currently, the "Database search" and "Solr search"
modules supports this.



Information for users
---------------------

For creating a facetted search, you first need a search. Create or find some
page displaying search results, either via a search page, a view or by any
other means. Now go to the configuration page for the index on which this search
is executed.
If this module is enabled, you'll notice a "Facets" tab. Click it and it will
take you to the index' facet configuration page. If the server the index is
lying on supports facets, you'll see a table containing all indexed fields and
some options for creating, renaming and enabling facet blocks. Otherwise you'll
just see a warning message that the server currently doesn't support facets. In
the latter case, you have to move the index to a server that does support them.

Now, ignoring for the moment the "Clone block" and "Delete data" options, just
select names for the facet blocks you'd like to add to the search and check
"Enabled". Since the names can be overridden on the block configuration page
anyways, you should just choose names that make finding the blocks easy.
Therefore, the default names usually work pretty well.
Save the settings and proceed to the block administration overview. There, just
move the blocks to a region of your liking – and you are done! The search page
should now display the facet blocks when a search was executed.

- Block configuration

Besides possibly adding some custom visibility settings, one thing you'll
probably want to do is change the block title for users. "Filter by author"
just reads better than "Node index: Filter on Author » User name".
Then, there are several options specific to facet blocks: the maximum number of
facet terms displayed, how many results they all need to have at least, how they
should be sorted and if a facet term for all items without a corresponding
field value should be created.
If there are searches with different IDs for the associated index, and facet
blocks have been displayed for more than one of them, you will also be given the
choice of limiting the display of the facet block based on the executed search's
ID. Just for the page-based filtering offered by the block module, you can
choose to either enable or disable all unselected searches by default. The
search ID should usually give a good clue about the kind of search it
represents.

- Cloning blocks

In some cases, the need might arise to create two facet blocks for the same
field on the same index. For example, you might want to display less terms for
some searches, or there might be two searches on the same index on a single
page, for which you want to create seperate facets. (To do the latter, you'll
have to specifically exclude one block from all searches into which you have
included the other.)
Doing this is very simple. On the index' facet configuration page, just check
the "Clone block" checkbox next to the facet block you want to create a copy of,
click "Save settings" and you are ready to go. Now you can just configure,
enable/disable and delete them seperately.

- Deleting data

While never strictly necessary, it might be a good idea to delete the settings
of facet blocks which you know you won't need anymore, if just to save on
database size. (For all other purposes, disabling the block has the same
effect.) To do that, just check "Delete data" on the index' facet configuration
page, next to the disabled facet block. Click "Save settings" and all of the
corresponding facet block settings will be reset to the defaults. This won't
influence any other facet blocks, not even ones on the same field.
If the index isn't lying on a server that supports facets anymore, you will also
be given the choice to delete the settings of all of its facet blocks at once.
Use this, if you are sure that you won't use facets with this index again (or
won't need the old settings, then).

- Issues

If you find any bugs or shortcomings while using this module, please file an
issue in the project's issue queue [1], using the "Facets" component.

[1] http://drupal.org/project/issues/search_api



Information for developers
--------------------------

If you are the developer of a SearchApiServiceInterface implementation and want
to support facets with your service class, too, you'll have to support the
"search_api_facets" feature. You can find details about the necessary additions
to your class in the search_api_facets.api.php file. In short, you'll just,
when executing a query, have to return facet terms and counts according to the
query's "search_api_facets" option, if present.
In order for the module to be able to tell that your server supports facets,
you will also have to change your service's supportsFeature() method to
something like the following:
  public function supportsFeature($feature) {
    return $feature == 'search_api_facets';
  }


The module uses one hidden variable, "search_api_facets_search_ids", to keep
track of the search IDs of searches executed for a given index. It is only
updated when a facet is displayed for the respective search, so isn't really a
reliable measure for this.
In any case, if you e.g. did some test searches and now don't want them to show
up in the block configuration forever after, just
variable_del("search_api_facets_search_ids").
