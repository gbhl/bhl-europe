Search API
----------

This module provides a framework for easily creating searches on any entity
known to Drupal, using any kind of search engine. For site administrators, it is
a great alternative to other search solutions, since it already incorporates
facetting support and the ability to use the Views module for displaying search
results, filters, etc. Also, with the Apache Solr integration [1], a high-performance
search engine is available for use with the Search API.

If you need help with the module, please post to the project's issue queue [2].

[1] http://drupal.org/project/search_api_solr
[2] http://drupal.org/project/issues/search_api


Content:
 - Glossary
 - Information for users
 - Information for developers
 - Included components


Glossary
--------

Terms as used in this module.

- Service class:
  A type of search engine, e.g. using the database, Apache Solr,
  Sphinx or any other professional or simple indexing mechanism. Takes care of
  the details of all operations, especially indexing or searching content.
- Server:
  One specific place for indexing data, using a set service class. Can
  e.g. be some tables in a database, a connection to a Solr server or other
  external services, etc.
- Index:
  One set of data for searching a specific entity. What and how data is
  indexed is determined by its settings. Also keeps track of which items still
  need to be indexed (or re-indexed, if they were updated). Needs to lie on a
  server in order to be really used (although configuration is independent of a
  server).
- Entity:
  One object of data, usually stored in the database. Might for example
  be a node, a user or a file.
- Field:
  A defined property of an entity, like a node's title or a user's mail address.
  All fields have defined datatypes. However, for indexing purposes the user
  might choose to index a property under a different data type than defined.
- Data type:
  Determines how a field is indexed. While "Fulltext" fields can be completely
  searched for keywords, other fields can only be used for filtering. They will
  also be converted to fit their respective value ranges.
  How types other than "Fulltext" are handled depends on the service class used.
  Its documentation should state how the type-selection affect the indexed
  content. However, service classes will always be able to handle all data
  types, it is just possible that the type doesn't affect the indexing at all
  (apart from "Fulltext vs. the rest").
- Boost:
  Number determining how important a certain field is, when searching for
  fulltext keywords. The higher the value is, the more important is the field.
  E.g., when the node title has a boost of 5.0 and the node body a boost of 1.0,
  keywords found in the title will increase the score as much as five keywords
  found in the body. Of course, this has only an effect when the score is used
  (for sorting or other purposes). It has no effect on other parts of the search
  result.
- Data alteration:
  A component that is used when indexing data. It can add additional fields to
  the indexed entity or prevent certain entities from being indexed. Fields
  added by callbacks have to be enabled on the "Fields" page to be of any use,
  but this is done by default.
- Processor:
  An object that is used for preprocessing indexed data as well as search
  queries, and for postprocessing search results. Usually only work on fulltext
  fields to control how content is indexed and searched. E.g., processors can be
  used to make searches case-insensitive, to filter markup out of indexed
  content, etc.


Information for users
---------------------

As stated above, you will need at least one other module to use the Search API,
namely one that defines a service class (e.g. search_api_db ("Database search"),
provided with this module).

- Creating a server
  (Configuration > Search API > Add server)

The most basic thing you have to create is a search server for indexing content.
Go to Configuration > Search API in the administration pages and select
"Add server". Name and description are usually only shown to administrators and
can be used to differentiate between several servers, or to explain a server's
use to other administrators (for larger sites). Disabling a server makes it
unusable for indexing and searching and can e.g. be used if the underlying
search engine is temporarily unavailable.
The "service class" is the most important option here, since it lets you select
which backend the search server will use. This cannot be changed after the
server is created.
Depending on the selected service class, further, service-specific settings will
be available. For details on those settings, consult the respective service's
documentation.

- Creating an index
  (Configuration > Search API > Add index)

For adding a search index, choose "Add index" on the Search API administration
page. Name, description and "enabled" status serve the exact same purpose as
for servers.
The most important option in this form is the indexed entity type. Every index
contains data on only a single type of entities, e.g. nodes, users or taxonomy
terms. This is therefore the only option that cannot be changed afterwards.
The server on which the index lies determines where the data will actually be
indexed. It doesn't affect any other settings of the index and can later be
changed with the only drawback being that the index' content will have to be
indexed again. You can also select a server that is at the moment disabled, or
choose to let the index lie on no server at all, for the time being. Note,
however, that you can only create enabled indexes on an enabled server. Also,
disabling a server will disable all indexes that lie on it.
Lastly, the cron limit option allows you to set whether, and how many, items
will be indexed for this index when cron runs (and the index is enabled). Items
can also be indexed manually, so even if this is set to 0, the index can still
be used.

- Index workflow
  (Configuration > Search API > [Index name] > Workflow)

This page lets you customize how the created index works, and what metadata will
be available, by selecting data alterations and processors (see the glossary for
further explanations).
Data alterations usually only add one or more fields to the entity and their
order is mostly irrelevant.
The order of processors, however, often is important. Read the processors'
descriptions or consult their documentation for determining how to use them most
effectively.

- Indexed fields
  (Configuration > Search API > [Index name] > Fields)

Here you can select which of the entities' fields will be indexed, and how.
Fields added by (enabled) data alterations will be available here, too.
Without selecting fields to index, the index will be useless and also won't be
available for searches. Select the "Fulltext" data type for fields which you
want search for keywords, and other data types when you want to use the field
for filtering (e.g., as facets). The "Item language" field will always be
indexed as it contains important information for processors and hooks.
You can also add fields of related entities here, via the "Add related fields"
form at the bottom of the page. For instance, you might want to index the
author's username to the indexed data of a node, and you need to add the "Body"
entity to the node when you want to index the actual text it contains.

- Index status
  (Configuration > Search API > [Index name] > Status)

On this page you can view how much of the entities are already indexed and also
control indexing. With the "Index now" button (displayed only when there are
still unindexed items) you can directly index a certain number of "dirty" items
(i.e., items not yet indexed in their current state). Setting "-1" as the number
will index all of those items, similar to the cron limit setting.
When you change settings that could affect indexing, and the index is not
automatically marked for re-indexing, you can do this manually with the
"Re-index content" button. All items in the index will be marked as dirty and be
re-indexed when subsequently indexing items (either manually or via cron runs).
Until all content is re-indexed, the old data will still show up in searches.
This is different with the "Clear index" button. All items will be marked as
dirty and additionally all data will be removed from the index. Therefore,
searches won't show any results until items are re-indexed, after clearing an
index. Use this only if completely wrong data has been indexed. It is also done
automatically when the index scheme or server settings change too drastically to
keep on using the old data.


Information for developers
--------------------------

 | NOTE:
 | For modules providing new entities: In order for your entities to become
 | searchable with the Search API, your module will need to implement
 | hook_entity_property_info() in addition to the normal hook_entity_info().
 | hook_entity_property_info() is documented in the entity module.
 | For custom field types to be available for indexing, provide a
 | "property_type" key in hook_field_info(), and optionally a callback at the
 | "property_callbacks" key.
 | Both processes are explained in [1].
 |
 | [1] http://drupal.org/node/1021466

Apart from improving the module itself, developers can extend search
capabilities provided by the Search API by providing implementations for one (or
several) of the following classes. Detailled documentation on the methods that
need to be implemented are always available as doc comments in the respective
interface definition (all found in their respective files in the includes/
directory). The details for hooks can be looked up in the search_api.api.php
file.
For all interfaces there are handy base classes which can (but don't need to) be
used to ease custom implementations, since they provide sensible generic
implementations for many methods. They, too, should be documented well enough
with doc comments for a developer to find the right methods to override or
implement.

- Service class
  Interface: SearchApiServiceInterface
  Base class: SearchApiAbstractService
  Hook: hook_search_api_service_info()

The service classes are the heart of the API, since they allow data to be
indexed on different search servers. Since these are quite some work to get
right, you should probably make sure a service class for a specific search
engine doesn't exist already before programming it yourself.
When your module supplies a service class, please make sure to provide
documentation (at least a README.txt) that clearly states the datatypes it
supports (and in what manner), how a direct query (a query where the keys are
a single string, instead of an array) is parsed and possible limitations of the
service class.
The central methods here are the indexItems() and the search() methods, which
always have to be overridden manually. The configurationForm() method allows
services to provide custom settings for the user.
See the SearchApiDbService class for an example implementation.

- Query class
  Interface: SearchApiQueryInterface
  Base class: SearchApiQuery

You can also override the query class' behaviour for your service class. You
can, for example, change key parsing behaviour, add additional parse modes
specific to your service, or override methods so the information is stored more
suitable for your service.
For the query class to become available (other than through manual creation),
you need a custom service class where you override the query() method to return
an instance of your query class.

- Data-alter callbacks
  Documented in example_random_alter() in the search_api.api.php file
  Hook: hook_search_api_alter_callback_info()

Data alter callbacks aren't objects, but simple functions that take an index
object and the items to alter (by reference) as parameters and should return
information on all added fields in the format expected by
hook_entity_property_info(). They are only called when indexing, or when
selecting the fields to index (in the latter case, with an empty array). For
adding additional information to search results, you have to use a processor.
See the data-alter callbacks in search_api.module for examples.
Data-alter callbacks are called "data alterations" in the UI.

- Processors
  Interface: SearchApiProcessorInterface
  Base class: SearchApiAbstractProcessor
  Hook: hook_search_api_processor_info()

Processors are used for altering the data when indexing or searching. The exact
specifications are available in the interface's doc comments. Just note that the
processor description should clearly state assumptions or restrictions on input
types (e.g. only tokenized text), item language, etc. and explain concisely what
effect it will have on searches.
See the processors in includes/processor.inc for examples.


Included components
-------------------

- Service classes

  * Database search
    A search server implementation that uses the normal database for indexing
    data. It isn't very fast and the results might also be less accurate than
    with third-party solutions like Solr, but it's very easy to set up and good
    for smaller applications or testing.
    See contrib/search_api_db/README.txt for details.

- Data alterations

  * URL field
    Provides a field with the URL for displaying the entity.
  * Fulltext field
    Offers the ability to add additional fulltext fields to the entity,
    containing the data from one or more other fields. Use this, e.g., to have a
    single field containing all data that should be searchable, or to make the
    text from a string field, like a taxonomy term, also fulltext-searchable.
  * Bundle filter
    Enables the admin to prevent entities from being indexed based on their
    bundle (content type for nodes, vocabulary for taxonomy terms, etc.).
  * Complete entity view
    Adds a field containing the whole HTML content of the entity as it is viewed
    on the site. The view mode used can be selected.
    Note, however, that this might not work for entities of all types. All core
    entities except files are supported, though.

- Processors

  * Ignore case
    Makes all fulltext searches (and, optionally, also filters on string values)
    case-insensitive. Some servers might do this automatically, for others this
    should probably always be activated.
  * HTML filter
    Strips HTML tags from fulltext fields and decodes HTML entities. If you are
    indexing HTML content (like node bodies) and the search server doesn't
    handle HTML on its own, this should be activated to avoid indexing HTML
    tags, as well as to give e.g. terms appearing in a heading a higher boost.
  * Tokenizer
    This processor allows you to specify how indexed fulltext content is split
    into seperate tokens – which characters are ignored and which treated as
    white-space that seperates words.

- Additional modules

  * Search pages
    This module lets you create simple search pages for indexes.
  * Search views
    This integrates the Search API with the Views module [1], enabling the user
    to create views which display search results from any Search API index.
  * Search facets
    For service classes supporting this feature (e.g. Solr search), this module
    automatically provides configurable facet blocks on pages that execute
    a search query.

[1] http://drupal.org/project/views
