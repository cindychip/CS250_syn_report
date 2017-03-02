#!/bin/bash

ssh -o StrictHostKeyChecking=no icluster1.EECS.Berkeley.EDU "ssh -o StrictHostKeyChecking=no icluster1.EECS.Berkeley.EDU \"exit\""
ssh -o StrictHostKeyChecking=no icluster2.EECS.Berkeley.EDU "ssh -o StrictHostKeyChecking=no icluster1.EECS.Berkeley.EDU \"exit\""
ssh -o StrictHostKeyChecking=no icluster3.EECS.Berkeley.EDU "ssh -o StrictHostKeyChecking=no icluster1.EECS.Berkeley.EDU \"exit\""
ssh -o StrictHostKeyChecking=no icluster4.EECS.Berkeley.EDU "ssh -o StrictHostKeyChecking=no icluster1.EECS.Berkeley.EDU \"exit\""
