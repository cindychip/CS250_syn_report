#!/bin/bash

ACCOUNT=`whoami`

ssh icluster1.EECS.Berkeley.EDU "rm -r /scratch/$ACCOUNT/hammer-data"
ssh icluster2.EECS.Berkeley.EDU "rm -r /scratch/$ACCOUNT/hammer-data"
ssh icluster3.EECS.Berkeley.EDU "rm -r /scratch/$ACCOUNT/hammer-data"
ssh icluster4.EECS.Berkeley.EDU "rm -r /scratch/$ACCOUNT/hammer-data"
