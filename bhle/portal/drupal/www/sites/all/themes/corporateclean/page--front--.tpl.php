<!-- Header. -->
<div id="header">
    <div id="header-inside">
 
        <div id="header-inside-left">
            
            <?php if ($logo): ?>           
              <!-- <h1> -->
              <a href="<?php print check_url($front_page); ?>" title="<?php print t('Home'); ?>"  id="logo">
              <?php // print $title ?>
              <img src="<?php print $logo; ?>" alt="<?php print t('Home'); ?>" /></a>
              <!-- </h1> -->
            <?php endif; ?>
    
            <p id="skip-link">
            <!-- Skryty odkaz vedouci na napovedu ke klavesovym zkratkam -->
            <a href="http://www.ippi.cz/klavesove-zkratky/neni-mapa-stranek.html" accesskey="1" tabindex="1" class="skiplink">Klávesové zkratky na tomto webu - rozšírené</a>
            <a href="/" accesskey="2" tabindex="3" class="skiplink">Hlavní strana</a>
            <a href="/#obsah" class="skiplink" tabindex="2" accesskey="0">Na informační obsah stránky</a>
            </p>
                 
        </div>
            
        <div id="header-inside-right">
        
      		<?php print render($page['header_area']); ?>    

          <!-- Header Menu. -->
          <div id="header-menu">

            <div id="header-menu-inside">
            <?php 
          	$main_menu_tree = menu_tree(variable_get('menu_main_links_source', 'main-menu')); 
          	print drupal_render($main_menu_tree);
	          ?>
            </div><!-- EOF: #header-menu-inside -->

        </div><!-- EOF: #header-menu -->
        
      </div>
    
  </div><!-- EOF: #header-inside -->

</div><!-- EOF: #header -->

<!-- Top text. -->
<div id="top_text">
	<?php print render($page['top_text']); ?>
</div><!-- EOF: #top_text -->


<!-- Content. -->
<div id="content">

    <div id="content-inside" class="inside">
    
        <div id="main">
                     
            <?php if ($page['highlighted']): ?><div id="highlighted"><?php print render($page['highlighted']); ?></div><?php endif; ?>
       
            <?php if ($messages): ?>
            <div id="console" class="clearfix">
            <?php print $messages; ?>
            </div>
            <?php endif; ?>
     
            <?php if ($page['help']): ?>
            <div id="help">
            <?php print render($page['help']); ?>
            </div>
            <?php endif; ?>
            
            <?php if ($action_links): ?>
            <ul class="action-links">
            <?php print render($action_links); ?>
            </ul>
            <?php endif; ?>
            
            
            <?php if ($tabs): ?><?php print render($tabs); ?><?php endif; ?>
            
            <?php print render($page['content']); ?>
            
            <?php print $feed_icons; ?>
            
        </div><!-- EOF: #main -->

        <div id="sidebar">
             
            <?php print render($page['sidebar_first']); ?>
            
            <?php print theme('links__system_secondary_menu', array('links' => $secondary_menu, 'attributes' => array('class' => array('secondary-menu', 'links', 'clearfix')))); ?>
            

        </div><!-- EOF: #sidebar -->

    </div><!-- EOF: #content-inside -->

        
        <!-- Banner. -->
        <div id="banner">
	       <?php print render($page['banner']); ?>
        </div><!-- EOF: #banner -->        
        
        <!-- Content bottom. -->
        <div id="content_bottom">
	       <?php print render($page['content_bottom']); ?>
        </div><!-- EOF: #content_bottom -->


</div><!-- EOF: #content -->

<!-- Footer -->    
<div id="footer">

    <div id="footer-inside">
    
        <div class="footer-area first">
        <?php print render($page['footer_first']); ?>
        </div><!-- EOF: .footer-area -->
        
        <div class="footer-area second">
        <?php print render($page['footer_second']); ?>
        </div><!-- EOF: .footer-area -->
        
        <div class="footer-area third">
        <?php print render($page['footer_third']); ?>
        </div><!-- EOF: .footer-area -->
       
    </div><!-- EOF: #footer-inside -->

</div><!-- EOF: #footer -->

<!-- Footer -->    
<div id="footer-bottom">

    <div id="footer-bottom-inside">
    
    	<div id="footer-bottom-left">
                    
            <?php print render($page['footer']); ?>
            
        </div>
        
        <div id="footer-bottom-right">
        
        	<?php print render($page['footer_bottom_right']); ?>
        
        </div><!-- EOF: #footer-bottom-right -->
       
    </div><!-- EOF: #footer-bottom-inside -->

</div><!-- EOF: #footer -->

