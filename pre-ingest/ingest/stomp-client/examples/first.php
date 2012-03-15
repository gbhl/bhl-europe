<?php
/**
 *
 * Copyright (C) 2009 Progress Software, Inc. All rights reserved.
 * http://fusesource.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// include a library
require_once("Stomp.php");
// make a connection
$con = new Stomp("tcp://localhost:61613");
// connect
$con->connect();
// send a message to the queue
$con->send("/queue/test", "test");
echo "Sent message with body 'test'\n";
// subscribe to the queue
$con->subscribe("/queue/test");
// receive a message from the queue
$msg = $con->readFrame();

// do what you want with the message
if ( $msg != null) {
    echo "Received message with body '$msg->body'\n";
    // mark the message as received in the queue
    $con->ack($msg);
} else {
    echo "Failed to receive a message\n";
}

// disconnect
$con->disconnect();
?>