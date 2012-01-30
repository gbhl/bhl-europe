
use int_pi_pi;

insert into users (
    user_id,
    user_name,
    user_pwd,
    user_content_home, 
    user_content_id,
    is_admin,
    user_config,
    user_config_smt,
    user_memo,
    user_directory,
    queue_mode,
    metadata_ws) 
    values (
        # ----------------------------------------------
        # select max(user_id)+1 from users
        # ----------------------------------------------
        ?,
        'NHM1',
        password('test'),
        'testdata',
        'NHM1',
        0,
        '',
        '-m c -cm 5 -if <input_file> -of <output_file>',
        'empty memo',
        '',
        0,
        ''
);
