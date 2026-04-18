import os
import sys

from loguru import logger

import config


logger.remove()
logger.add(sys.stderr, format="<green>{time:YYYY-MM-DD HH:mm:ss.SSS}</green> | <level>{level:<8}</level> | <level>{message}</level>", level="TRACE")


# 폴더 생성
logger.info("Making Folder...")
def mkdir(folder_path):
    if not os.path.exists(folder_path):
        os.makedirs(folder_path)
        logger.debug(f"Made Folder ({os.path.abspath(folder_path)})")
    else:
        logger.debug(f"Already Existed Folder ({os.path.abspath(folder_path)})")
mkdir(config.MODEL_PATH)
mkdir(config.MNIST_DATASET_PATH)
logger.debug("Finished Making Folder")
